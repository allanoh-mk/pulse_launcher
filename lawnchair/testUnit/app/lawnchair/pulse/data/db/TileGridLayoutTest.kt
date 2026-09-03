package app.lawnchair.pulse.data.db

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TileGridLayoutTest {

    @Test
    fun `isValidSpan accepts valid 1, 2, 4 spans`() {
        assertTrue(TileGridLayout.isValidSpan(1, 1))
        assertTrue(TileGridLayout.isValidSpan(2, 1))
        assertTrue(TileGridLayout.isValidSpan(2, 2))
        assertTrue(TileGridLayout.isValidSpan(4, 2))
        assertTrue(TileGridLayout.isValidSpan(4, 1))
    }

    @Test
    fun `isValidSpan rejects spans exceeding columns or invalid numbers`() {
        assertFalse(TileGridLayout.isValidSpan(3, 1))
        assertFalse(TileGridLayout.isValidSpan(5, 1))
        assertFalse(TileGridLayout.isValidSpan(0, 1))
        assertFalse(TileGridLayout.isValidSpan(1, 3))
    }

    @Test
    fun `packTiles places non-overlapping coordinates without collision`() {
        val tiles = listOf(
            TileConfig(id = "clock", tileType = TileType.CLOCK, spanX = 2, spanY = 1, sortOrder = 0),
            TileConfig(id = "weather", tileType = TileType.WEATHER, spanX = 2, spanY = 1, sortOrder = 1),
            TileConfig(id = "media", tileType = TileType.MEDIA, spanX = 4, spanY = 2, sortOrder = 2),
        )

        val packed = TileGridLayout.packTiles(tiles)

        // Clock should be at (col=0, row=0)
        assertEquals(0 to 0, packed["clock"])
        // Weather fills remaining 2 columns in row 0: (col=2, row=0)
        assertEquals(2 to 0, packed["weather"])
        // Media spans 4 columns, so it moves to row 1: (col=0, row=1)
        assertEquals(0 to 1, packed["media"])
    }
}
