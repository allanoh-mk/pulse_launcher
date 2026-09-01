package app.lawnchair.pulse.workspace

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AlphabetIndexMathTest {

    @Test
    fun `touch at top maps to first letter`() {
        assertEquals(0, AlphabetIndexMath.letterIndexForTouch(0f, letterCount = 26))
    }

    @Test
    fun `touch at bottom maps to last letter`() {
        assertEquals(25, AlphabetIndexMath.letterIndexForTouch(1f, letterCount = 26))
    }

    @Test
    fun `touch fraction outside 0 to 1 is clamped`() {
        assertEquals(0, AlphabetIndexMath.letterIndexForTouch(-5f, letterCount = 26))
        assertEquals(25, AlphabetIndexMath.letterIndexForTouch(5f, letterCount = 26))
    }

    @Test
    fun `zero letters never divides by zero`() {
        assertEquals(0, AlphabetIndexMath.letterIndexForTouch(0.5f, letterCount = 0))
    }

    @Test
    fun `focused letter gets full max scale`() {
        val scale = AlphabetIndexMath.waveScale(letterIndex = 5, focusedIndex = 5, maxScale = 1.8f)
        assertEquals(1.8f, scale, 0.001f)
    }

    @Test
    fun `letters further from focus shrink toward 1`() {
        val near = AlphabetIndexMath.waveScale(letterIndex = 4, focusedIndex = 5)
        val far = AlphabetIndexMath.waveScale(letterIndex = 15, focusedIndex = 5)
        assertTrue(near > far)
        assertTrue(far < 1.05f)
    }

    @Test
    fun `no focus means neutral scale everywhere`() {
        assertEquals(1f, AlphabetIndexMath.waveScale(letterIndex = 3, focusedIndex = null), 0.001f)
    }
}
