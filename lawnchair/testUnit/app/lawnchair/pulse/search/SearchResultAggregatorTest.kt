package app.lawnchair.pulse.search

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class SearchResultAggregatorTest {

    @Test
    fun `search result model retains id type and action data`() {
        val result = SearchResult(
            id = "app_1",
            title = "Spotify",
            subtitle = "Music Player",
            type = ResultType.APP,
            actionData = "com.spotify.music",
        )
        assertEquals("app_1", result.id)
        assertEquals("Spotify", result.title)
        assertEquals("Music Player", result.subtitle)
        assertEquals(ResultType.APP, result.type)
        assertEquals("com.spotify.music", result.actionData)
    }

    @Test
    fun `calculation result model holds formatted numbers`() {
        val mathQuery = "12 * 12"
        val evaluation = MathEvaluator.evaluate(mathQuery)
        assertNotNull(evaluation)

        val result = SearchResult(
            id = "calc_$mathQuery",
            title = evaluation!!,
            subtitle = "Calculation = $mathQuery",
            type = ResultType.CALCULATION,
            actionData = evaluation,
        )

        assertEquals("144", result.title)
        assertEquals(ResultType.CALCULATION, result.type)
    }
}
