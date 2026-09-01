package app.lawnchair.pulse.iconstudio

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BlendMode
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.LruCache
import app.lawnchair.icons.shape.IconShape

data class IconRenderConfig(
    val shape: IconShapePreset,
    val style: IconStyle,
    val sizeScale: Float = 1f,
)

/**
 * Every custom-styled icon surface in Pulse (workspace tiles, app list rows,
 * search results, the Icon Studio preview) renders through this single entry
 * point, per the "one IconRenderer, every surface calls render()" design
 * intent in docs/02-icon-studio.md.
 */
object IconRenderer {

    private const val CANVAS_SIZE = 192
    private val cache = LruCache<String, Bitmap>(64)

    fun render(context: Context, packageName: String, baseIcon: Drawable, config: IconRenderConfig): Bitmap {
        val cacheKey = "$packageName-${config.shape}-${config.style}-${config.sizeScale}"
        cache.get(cacheKey)?.let { return it }

        val bitmap = Bitmap.createBitmap(CANVAS_SIZE, CANVAS_SIZE, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val maskPath = maskPathFor(context, config.shape, CANVAS_SIZE.toFloat())
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // 1. Clip to the shape mask so everything drawn after this respects the boundary.
        canvas.save()
        canvas.clipPath(maskPath)

        // 2. Draw the original app icon, scaled to fill the canvas (minus inset for sizeScale).
        val inset = CANVAS_SIZE * (1f - config.sizeScale) / 2f
        val iconBounds = RectF(inset, inset, CANVAS_SIZE - inset, CANVAS_SIZE - inset)
        drawDrawableInto(baseIcon, canvas, iconBounds)

        // 3. Apply the requested post-processing style within the clipped bounds.
        when (config.style) {
            IconStyle.FLAT -> Unit
            IconStyle.LIQUID_GLASS -> applyLiquidGlass(canvas, paint)
            IconStyle.NEON -> applyNeon(canvas, paint)
            IconStyle.EMBOSSED -> applyEmbossed(canvas, paint)
        }
        canvas.restore()

        // 4. Darkened outline rim sits outside the clip, tracing the mask edge (iOS 27 spec).
        if (config.style == IconStyle.LIQUID_GLASS) {
            drawEdgeOutline(canvas, maskPath)
        }

        cache.put(cacheKey, bitmap)
        return bitmap
    }

    fun asDrawable(context: Context, packageName: String, baseIcon: Drawable, config: IconRenderConfig): Drawable =
        BitmapDrawable(context.resources, render(context, packageName, baseIcon, config))

    fun clearCache() = cache.evictAll()

    private fun maskPathFor(context: Context, shape: IconShapePreset, size: Float): Path {
        val unitPath = when (shape) {
            IconShapePreset.CIRCLE -> IconShape.Circle.getMaskPath()
            IconShapePreset.SQUIRCLE -> IconShape.Squircle.getMaskPath()
            IconShapePreset.SYSTEM -> IconShape.Circle.getMaskPath()
        }
        val scaled = Path(unitPath)
        val matrix = Matrix().apply { setScale(size / 100f, size / 100f) }
        scaled.transform(matrix)
        return scaled
    }

    private fun drawDrawableInto(drawable: Drawable, canvas: Canvas, bounds: RectF) {
        val previousBounds = drawable.copyBounds()
        drawable.setBounds(bounds.left.toInt(), bounds.top.toInt(), bounds.right.toInt(), bounds.bottom.toInt())
        drawable.draw(canvas)
        drawable.bounds = previousBounds
    }

    /** Bright specular top rim + darkened bottom, per docs/research/ios_feel_style_liquid_glass.txt Section 4. */
    private fun applyLiquidGlass(canvas: Canvas, paint: Paint) {
        val size = CANVAS_SIZE.toFloat()
        paint.reset()
        paint.isAntiAlias = true
        paint.shader = LinearGradient(
            0f, 0f, 0f, size,
            Color.argb(90, 255, 255, 255),
            Color.argb(0, 255, 255, 255),
            Shader.TileMode.CLAMP,
        )
        canvas.drawRect(0f, 0f, size, size * 0.5f, paint)

        paint.shader = LinearGradient(
            0f, size * 0.7f, 0f, size,
            Color.argb(0, 0, 0, 0),
            Color.argb(60, 0, 0, 0),
            Shader.TileMode.CLAMP,
        )
        canvas.drawRect(0f, size * 0.7f, size, size, paint)
    }

    private fun applyNeon(canvas: Canvas, paint: Paint) {
        val size = CANVAS_SIZE.toFloat()
        paint.reset()
        paint.isAntiAlias = true
        paint.color = Color.argb(140, 0, 255, 210)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = size * 0.06f
        canvas.drawRect(
            paint.strokeWidth / 2,
            paint.strokeWidth / 2,
            size - paint.strokeWidth / 2,
            size - paint.strokeWidth / 2,
            paint
        )

        paint.style = Paint.Style.FILL
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.DARKEN)
        paint.shader = null
        paint.color = Color.argb(40, 0, 0, 0)
        canvas.drawRect(0f, 0f, size, size, paint)
        paint.xfermode = null
    }

    private fun applyEmbossed(canvas: Canvas, paint: Paint) {
        val size = CANVAS_SIZE.toFloat()
        paint.reset()
        paint.isAntiAlias = true
        paint.blendMode = BlendMode.OVERLAY
        paint.shader = LinearGradient(
            0f, 0f, 0f, size,
            Color.argb(120, 255, 255, 255),
            Color.argb(120, 0, 0, 0),
            Shader.TileMode.CLAMP,
        )
        canvas.drawRect(0f, 0f, size, size, paint)
        paint.blendMode = null
    }

    private fun drawEdgeOutline(canvas: Canvas, maskPath: Path) {
        val outline = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = CANVAS_SIZE * 0.015f
            color = Color.argb(38, 0, 0, 0) // ~15% opacity dark edge, per iOS 27 spec
        }
        canvas.drawPath(maskPath, outline)
    }
}
