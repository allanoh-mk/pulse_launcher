package app.lawnchair.pulse.data.db

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TileGridLayoutTest {

    @Test
    fun `valid spans are 1, 2 or 4 and fit within the column count`() {
        assertTrue(TileGridLayout.isValidSpan(1, 1))
        assertTrue(TileGridLayout.isValidSpan(2, 2))
        assertTrue(TileGridLayout.isValidSpan(4, 1))
        assertFalse(TileGridLayout.isValidSpan(3, 1))
        assertFalse(TileGridLayout.isValidSpan(8, 1))
    }

    @Test
    fun `single 1x1 tiles pack left to right along row zero`() {
        val tiles = listOf(
            TileConfig(id = "a", tileType = TileType.APP, spanX = 1, spanY = 1, sortOrder = 0),
            TileConfig(id = "b", tileType = TileType.APP, spanX = 1, spanY = 1, sortOrder = 1),
        )
        val packed = TileGridLayout.packTiles(tiles)
        assertEquals(0 to 0, packed["a"])
        assertEquals(1 to 0, packed["b"])
    }

    @Test
    fun `a 2x2 tile reserves its footprint so later tiles skip it`() {
        val tiles = listOf(
            TileConfig(id = "big", tileType = TileType.MEDIA, spanX = 2, spanY = 2, sortOrder = 0),
            TileConfig(id = "small", tileType = TileType.APP, spanX = 1, spanY = 1, sortOrder = 1),
        )
        val packed = TileGridLayout.packTiles(tiles)
        assertEquals(0 to 0, packed["big"])
        // Row 0, columns 0-1 are occupied by "big"; "small" must land at column 2, row 0.
        assertEquals(2 to 0, packed["small"])
    }

    @Test
    fun `a full-width 4x1 tile pushes the next tile to the next row`() {
        val tiles = listOf(
            TileConfig(id = "wide", tileType = TileType.WEATHER, spanX = 4, spanY = 1, sortOrder = 0),
            TileConfig(id = "next", tileType = TileType.APP, spanX = 1, spanY = 1, sortOrder = 1),
        )
        val packed = TileGridLayout.packTiles(tiles)
        assertEquals(0 to 0, packed["wide"])
        assertEquals(0 to 1, packed["next"])
    }
}
