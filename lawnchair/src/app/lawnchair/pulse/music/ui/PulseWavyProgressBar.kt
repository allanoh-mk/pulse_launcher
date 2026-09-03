package app.lawnchair.pulse.music.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.sin

/**
 * Signature Pulse Music Wavy Progress Bar.
 *
 * Renders an undulating sinusoidal audio wave during active playback that
 * smoothly flattens to a straight line when paused. Supports drag & tap scrubbing.
 */
@Composable
fun PulseWavyProgressBar(
    progress: Float,
    isPlaying: Boolean,
    onSeek: (Float) -> Unit,
    modifier: Modifier = Modifier,
    barHeight: Dp = 28.dp,
    strokeWidth: Dp = 4.dp,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
) {
    val infiniteTransition = rememberInfiniteTransition(label = "WavePhaseTransition")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1400, easing = LinearEasing),
        ),
        label = "WavePhase",
    )

    // Smoothly animate wave amplitude between active (undulating) and paused (flat)
    val targetAmplitude = if (isPlaying) 6f else 0f
    val amplitude by animateFloatAsState(
        targetValue = targetAmplitude,
        animationSpec = tween(durationMillis = 350),
        label = "WaveAmplitude",
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(barHeight)
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    val fraction = (offset.x / size.width).coerceIn(0f, 1f)
                    onSeek(fraction)
                }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    change.consume()
                    val fraction = (change.position.x / size.width).coerceIn(0f, 1f)
                    onSeek(fraction)
                }
            },
    ) {
        val width = size.width
        val midY = size.height / 2f
        val strokePx = strokeWidth.toPx()
        val playedWidth = width * progress.coerceIn(0f, 1f)

        // 1. Draw inactive background track (straight horizontal line)
        if (playedWidth < width) {
            drawLine(
                color = inactiveColor,
                start = Offset(playedWidth, midY),
                end = Offset(width, midY),
                strokeWidth = strokePx,
                cap = StrokeCap.Round,
            )
        }

        // 2. Draw active wavy progress path
        if (playedWidth > 0f) {
            val wavePath = Path()
            val wavelength = 60f // Pixels per full sine cycle
            val step = 3f

            wavePath.moveTo(0f, midY)
            var x = 0f
            while (x <= playedWidth) {
                val radians = (x / wavelength) * (2f * PI.toFloat()) - phase
                val y = midY + amplitude * sin(radians)
                wavePath.lineTo(x, y)
                x += step
            }

            drawPath(
                path = wavePath,
                color = activeColor,
                style = Stroke(width = strokePx, cap = StrokeCap.Round),
            )

            // Draw current playhead thumb indicator
            val thumbY = midY + amplitude * sin((playedWidth / wavelength) * (2f * PI.toFloat()) - phase)
            drawCircle(
                color = activeColor,
                radius = strokePx * 1.5f,
                center = Offset(playedWidth, thumbY),
            )
        }
    }
}
