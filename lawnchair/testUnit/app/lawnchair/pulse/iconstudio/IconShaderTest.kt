package app.lawnchair.pulse.iconstudio

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class IconShaderTest {

    @Test
    fun `icon style enum contains all required AGSL shader styles`() {
        val styles = IconStyle.values().toList()
        assertTrue(styles.contains(IconStyle.FLAT))
        assertTrue(styles.contains(IconStyle.LIQUID_GLASS))
        assertTrue(styles.contains(IconStyle.NEON))
        assertTrue(styles.contains(IconStyle.EMBOSSED))
        assertTrue(styles.contains(IconStyle.DUOTONE))
        assertTrue(styles.contains(IconStyle.HOLOGRAPHIC))
        assertTrue(styles.contains(IconStyle.FILM_GRAIN))
        assertTrue(styles.contains(IconStyle.MATERIAL_YOU))
        assertEquals(8, styles.size)
    }

    @Test
    fun `icon shape preset contains circle and squircle`() {
        val presets = IconShapePreset.values().toList()
        assertTrue(presets.contains(IconShapePreset.CIRCLE))
        assertTrue(presets.contains(IconShapePreset.SQUIRCLE))
        assertTrue(presets.contains(IconShapePreset.SYSTEM))
    }

    @Test
    fun `render config holds shape style and scale parameters`() {
        val config = IconRenderConfig(
            shape = IconShapePreset.SQUIRCLE,
            style = IconStyle.HOLOGRAPHIC,
            sizeScale = 0.95f,
        )
        assertEquals(IconShapePreset.SQUIRCLE, config.shape)
        assertEquals(IconStyle.HOLOGRAPHIC, config.style)
        assertEquals(0.95f, config.sizeScale, 0.001f)
    }
}
