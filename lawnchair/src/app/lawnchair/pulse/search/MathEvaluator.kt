package app.lawnchair.pulse.search

import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.sqrt

object MathEvaluator {

    private val numberFormatter = DecimalFormat("#,###.######")

    fun isMathExpression(input: String): Boolean {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) return false
        val containsOperator = trimmed.any { it in "+-*/x×÷%^" } || trimmed.startsWith("sqrt(")
        val containsDigits = trimmed.any { it.isDigit() }
        val validChars = trimmed.all { it.isDigit() || it in "+-*/x×÷%^()., \t" || it.isLetter() }
        return containsOperator && containsDigits && validChars
    }

    fun evaluate(expression: String): String? {
        return try {
            val normalized = expression
                .replace("×", "*")
                .replace("x", "*")
                .replace("÷", "/")
                .replace(",", "")
                .trim()

            val result = parseExpression(normalized)
            if (result.isNaN() || result.isInfinite()) null else numberFormatter.format(result)
        } catch (e: Exception) {
            null
        }
    }

    private fun parseExpression(expr: String): Double {
        var pos = -1
        var ch = 0

        fun nextChar() {
            ch = if (++pos < expr.length) expr[pos].code else -1
        }

        fun eat(charToEat: Int): Boolean {
            while (ch == ' '.code || ch == '\t'.code) nextChar()
            if (ch == charToEat) {
                nextChar()
                return true
            }
            return false
        }

        fun parseFactor(): Double {
            if (eat('+'.code)) return +parseFactor()
            if (eat('-'.code)) return -parseFactor()

            var x: Double
            val startPos = pos

            if (eat('('.code)) {
                x = parseFactor()
                while (ch == '+'.code || ch == '-'.code || ch == '*'.code || ch == '/'.code || ch == '^'.code) {
                    when {
                        eat('+'.code) -> x += parseFactor()
                        eat('-'.code) -> x -= parseFactor()
                        eat('*'.code) -> x *= parseFactor()
                        eat('/'.code) -> x /= parseFactor()
                        eat('^'.code) -> x = x.pow(parseFactor())
                    }
                }
                eat(')'.code)
            } else if ((ch in '0'.code..'9'.code) || ch == '.'.code) {
                while ((ch in '0'.code..'9'.code) || ch == '.'.code) nextChar()
                x = expr.substring(startPos, pos).toDouble()
            } else if (ch in 'a'.code..'z'.code) {
                while (ch in 'a'.code..'z'.code) nextChar()
                val func = expr.substring(startPos, pos)
                if (eat('('.code)) {
                    x = parseFactor()
                    eat(')'.code)
                } else {
                    x = parseFactor()
                }
                x = when (func) {
                    "sqrt" -> sqrt(x)
                    "abs" -> abs(x)
                    "round" -> round(x)
                    else -> throw RuntimeException("Unknown function: $func")
                }
            } else {
                throw RuntimeException("Unexpected: " + ch.toChar())
            }

            if (eat('^'.code)) x = x.pow(parseFactor())
            return x
        }

        fun parseTerm(): Double {
            var x = parseFactor()
            while (true) {
                when {
                    eat('*'.code) -> x *= parseFactor()
                    eat('/'.code) -> x /= parseFactor()
                    eat('%'.code) -> x %= parseFactor()
                    else -> return x
                }
            }
        }

        nextChar()
        var x = parseTerm()
        while (true) {
            when {
                eat('+'.code) -> x += parseTerm()
                eat('-'.code) -> x -= parseTerm()
                else -> return x
            }
        }
    }
}
