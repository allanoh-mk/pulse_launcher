package app.lawnchair.pulse.search

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UniversalSearchTest {

    @Test
    fun `search state initializes with hidden overlay and empty results`() {
        val state = SearchState()
        assertEquals(false, state.isVisible)
        assertEquals("", state.query)
        assertTrue(state.results.isEmpty())
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `result types include required table-stakes domains`() {
        val types = ResultType.values().toList()
        assertTrue(types.contains(ResultType.APP))
        assertTrue(types.contains(ResultType.CONTACT))
        assertTrue(types.contains(ResultType.FILE))
        assertTrue(types.contains(ResultType.WEB))
        assertTrue(types.contains(ResultType.CALCULATION))
    }

    @Test
    fun `offline math evaluator integrates with search state`() {
        val query = "50 * 20"
        val answer = MathEvaluator.evaluate(query)
        assertEquals("1,000", answer)

        val calcResult = SearchResult(
            id = "calc_$query",
            title = answer!!,
            subtitle = "Math",
            type = ResultType.CALCULATION,
        )
        val state = SearchState(isVisible = true, query = query, results = listOf(calcResult))
        assertEquals(1, state.results.size)
        assertEquals("1,000", state.results[0].title)
    }
}
