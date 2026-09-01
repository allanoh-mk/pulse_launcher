package app.lawnchair.pulse.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

@Composable
fun PulseSplashScreen(onAnimationFinished: () -> Unit) {
    var isVisible by remember { mutableStateOf(true) }

    val writeAnimation = remember { Animatable(0f) }
    val breatheAnimation = rememberInfiniteTransition()

    // 60-70 BPM is ~1 Hz.
    val strokeWidthPulse by breatheAnimation.animateFloat(
        initialValue = 18f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    LaunchedEffect(Unit) {
        // Write on: 3.5 seconds
        writeAnimation.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 3500, easing = FastOutSlowInEasing)
        )
        // Wait a bit, then fade out
        kotlinx.coroutines.delay(800)
        isVisible = false
        onAnimationFinished()
    }

    if (isVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color(0xFF1A1A1A), Color(0xFF0D0D0D)),
                        center = Offset.Unspecified,
                        radius = 2000f
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Calligraphic "Pulse" roughly generated path
                val path = Path().apply {
                    moveTo(size.width * 0.3f, size.height * 0.5f)
                    cubicTo(
                        size.width * 0.3f, size.height * 0.4f,
                        size.width * 0.4f, size.height * 0.4f,
                        size.width * 0.4f, size.height * 0.5f
                    )
                    cubicTo(
                        size.width * 0.4f, size.height * 0.6f,
                        size.width * 0.45f, size.height * 0.55f,
                        size.width * 0.5f, size.height * 0.5f
                    )
                    cubicTo(
                        size.width * 0.55f, size.height * 0.45f,
                        size.width * 0.6f, size.height * 0.5f,
                        size.width * 0.65f, size.height * 0.55f
                    )
                    cubicTo(
                        size.width * 0.7f, size.height * 0.6f,
                        size.width * 0.75f, size.height * 0.5f,
                        size.width * 0.8f, size.height * 0.45f
                    )
                }

                val pathMeasure = PathMeasure()
                pathMeasure.setPath(path, false)
                val length = pathMeasure.length
                
                val renderPath = Path()
                pathMeasure.getSegment(0f, length * writeAnimation.value, renderPath, true)

                drawPath(
                    path = renderPath,
                    color = Color.Black,
                    style = Stroke(
                        width = strokeWidthPulse,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }
    }
}
