package app.lawnchair.pulse.data.db

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class IconStyleConfigTest {

    @Test
    fun `global icon style configuration creates valid entity`() {
        val config = IconStyleConfig(
            packageName = GLOBAL_ICON_STYLE_KEY,
            shape = "SQUIRCLE",
            style = "LIQUID_GLASS",
            sizeScale = 1.0f,
            customLabel = null,
        )
        assertEquals(GLOBAL_ICON_STYLE_KEY, config.packageName)
        assertEquals("SQUIRCLE", config.shape)
        assertEquals("LIQUID_GLASS", config.style)
        assertEquals(1.0f, config.sizeScale, 0.001f)
        assertNull(config.customLabel)
    }

    @Test
    fun `per-app custom override stores individual app styling`() {
        val appConfig = IconStyleConfig(
            packageName = "com.spotify.music",
            shape = "CIRCLE",
            style = "NEON",
            sizeScale = 0.9f,
            customLabel = "Spotify Tunes",
        )
        assertEquals("com.spotify.music", appConfig.packageName)
        assertEquals("CIRCLE", appConfig.shape)
        assertEquals("NEON", appConfig.style)
        assertEquals("Spotify Tunes", appConfig.customLabel)
    }
}
