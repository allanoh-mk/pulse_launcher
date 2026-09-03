package app.lawnchair.pulse.search

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MathEvaluatorTest {

    @Test
    fun `isMathExpression identifies valid math strings`() {
        assertTrue(MathEvaluator.isMathExpression("24 * 1024"))
        assertTrue(MathEvaluator.isMathExpression("100 + 250"))
        assertTrue(MathEvaluator.isMathExpression("(50 - 10) / 2"))
        assertTrue(MathEvaluator.isMathExpression("sqrt(144)"))
        assertTrue(MathEvaluator.isMathExpression("2^8"))
    }

    @Test
    fun `isMathExpression rejects ordinary text and letters`() {
        assertFalse(MathEvaluator.isMathExpression("hello world"))
        assertFalse(MathEvaluator.isMathExpression("YouTube"))
        assertFalse(MathEvaluator.isMathExpression(""))
    }

    @Test
    fun `evaluates basic arithmetic correctly`() {
        assertEquals("24,576", MathEvaluator.evaluate("24 * 1024"))
        assertEquals("350", MathEvaluator.evaluate("100 + 250"))
        assertEquals("20", MathEvaluator.evaluate("(50 - 10) / 2"))
        assertEquals("12", MathEvaluator.evaluate("sqrt(144)"))
        assertEquals("256", MathEvaluator.evaluate("2^8"))
    }

    @Test
    fun `handles Unicode multiplication and division symbols`() {
        assertEquals("50", MathEvaluator.evaluate("10 × 5"))
        assertEquals("10", MathEvaluator.evaluate("50 ÷ 5"))
        assertEquals("24", MathEvaluator.evaluate("6 x 4"))
    }

    @Test
    fun `evaluates floating point division`() {
        assertEquals("2.5", MathEvaluator.evaluate("5 / 2"))
        assertEquals("10.5", MathEvaluator.evaluate("10 + 0.5"))
    }

    @Test
    fun `returns null for invalid syntax`() {
        assertNull(MathEvaluator.evaluate("5 ++-"))
        assertNull(MathEvaluator.evaluate("invalid"))
    }
}
