package app.lawnchair.pulse.workspace

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

@Composable
fun BentoFlipCard(
    frontContent: @Composable () -> Unit,
    backContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = remember { BentoFlipCardState() }

    val rotation by animateFloatAsState(
        targetValue = state.rotation,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = 300f
        ),
        label = "BentoFlip"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable { state.toggle() }
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            },
        contentAlignment = Alignment.Center
    ) {
        if (rotation <= 90f || rotation >= 270f) {
            // Front is visible
            Box(modifier = Modifier.fillMaxSize()) {
                frontContent()
            }
        } else {
            // Back is visible. Needs to be rotated 180 deg to prevent mirroring
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationY = 180f }
            ) {
                backContent()
            }
        }
    }
}
