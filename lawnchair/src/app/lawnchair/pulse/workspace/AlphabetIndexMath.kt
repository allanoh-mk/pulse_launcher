package app.lawnchair.pulse.workspace

import kotlin.math.abs
import kotlin.math.exp

/**
 * Pure math backing the vertical alphabet index sidebar (Slide 3, "wave" style
 * inspired by Niagara Launcher). Kept free of Compose/Android types so the
 * touch-to-letter mapping and per-letter scale "wave" can be unit tested.
 */
object AlphabetIndexMath {

    /**
     * Maps a normalized touch fraction (0f at the top of the index rail, 1f at
     * the bottom) to the letter index it corresponds to.
     */
    fun letterIndexForTouch(touchFraction: Float, letterCount: Int): Int {
        if (letterCount <= 0) return 0
        val clamped = touchFraction.coerceIn(0f, 1f)
        val index = (clamped * letterCount).toInt()
        return index.coerceIn(0, letterCount - 1)
    }

    /**
     * Computes a Gaussian-falloff scale multiplier for a letter at [letterIndex]
     * given the currently focused index [focusedIndex], producing the "wave"
     * magnification effect around the finger without needing frame-by-frame
     * spring simulation for neighboring letters.
     *
     * @param spread how many letters on either side feel the wave (larger = wider wave).
     * @param maxScale the multiplier applied exactly at the focused letter.
     */
    fun waveScale(
        letterIndex: Int,
        focusedIndex: Int?,
        spread: Float = 2.2f,
        maxScale: Float = 1.8f,
    ): Float {
        if (focusedIndex == null) return 1f
        val distance = abs(letterIndex - focusedIndex).toFloat()
        val falloff = exp(-(distance * distance) / (2f * spread * spread))
        return 1f + (maxScale - 1f) * falloff
    }
}
