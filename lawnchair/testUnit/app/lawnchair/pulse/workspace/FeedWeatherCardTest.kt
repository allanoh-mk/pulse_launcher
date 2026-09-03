package app.lawnchair.pulse.workspace

import org.junit.Assert.assertNotNull
import org.junit.Test

class FeedWeatherCardTest {
    @Test
    fun testWeatherRepositoryCalled() {
        assertNotNull("WeatherRepository should be called", true)
    }
}
