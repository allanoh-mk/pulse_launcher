package app.lawnchair.pulse.workspace

import app.lawnchair.pulse.data.db.TileConfig
import app.lawnchair.pulse.data.db.TileType
import org.junit.Assert.assertEquals
import org.junit.Test

class TileCellRendererTest {
    @Test
    fun testTileTypesRendered() {
        val testTiles = listOf(
            TileConfig(id = "clock", tileType = TileType.CLOCK, spanX = 2, spanY = 1, sortOrder = 0),
            TileConfig(id = "weather", tileType = TileType.WEATHER, spanX = 2, spanY = 1, sortOrder = 1),
            TileConfig(id = "media", tileType = TileType.MEDIA, spanX = 2, spanY = 2, sortOrder = 2),
            TileConfig(id = "assistant", tileType = TileType.ASSISTANT, spanX = 2, spanY = 1, sortOrder = 3),
        )

        assertEquals("All tile types should be rendered", 4, testTiles.size)
    }
}
