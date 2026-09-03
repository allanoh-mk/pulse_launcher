package app.lawnchair.pulse.workspace

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

/**
 * State controller for 3D flip card animations on Bento tiles.
 *
 * Smoothly flips between front primary complication (e.g. Current Weather, Now Playing)
 * and back secondary complication (e.g. 5-Day Forecast, Queue, Quick Settings).
 */
class BentoFlipCardState(
    initialFlipped: Boolean = false,
) {
    var isFlipped by mutableStateOf(initialFlipped)
        private set

    var rotation by mutableFloatStateOf(if (initialFlipped) 180f else 0f)
        private set

    fun toggle() {
        isFlipped = !isFlipped
        rotation = if (isFlipped) 180f else 0f
    }

    fun flipToFront() {
        if (isFlipped) {
            isFlipped = false
            rotation = 0f
        }
    }

    fun flipToBack() {
        if (!isFlipped) {
            isFlipped = true
            rotation = 180f
        }
    }

    /**
     * True when the card is past the 90-degree midpoint of rotation,
     * signaling that the back-facing complication content should be rendered.
     */
    val isShowingBackContent: Boolean
        get() = (rotation % 360f) in 90f..270f
}
