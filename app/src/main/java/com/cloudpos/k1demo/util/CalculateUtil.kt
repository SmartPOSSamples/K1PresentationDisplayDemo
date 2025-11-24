package com.cloudpos.k1demo.util

import com.cloudpos.k1demo.constants.NUM_PLUS
import com.cloudpos.k1demo.constants.NUM_POINTS
import java.math.BigDecimal



class CalculateUtil(private val sb: StringBuilder) {
    fun calcSum(sb: StringBuilder): String {
        val expr = sb.toString()
            .substringAfterLast('\n')
            .substringBefore('=')
            .trim()

        if (expr.isEmpty()) return ""

        val parts = expr.split("+").filter { it.isNotBlank() }
        var sum = BigDecimal.ZERO

        for (part in parts) {
            val value = part.toBigDecimalOrNull() ?: BigDecimal.ZERO
            sum = sum.add(value)
        }

        val result = sum.stripTrailingZeros()
        return result.toPlainString()
    }

    fun isLastNumPoint(content: String): Boolean {
        return sb.isNotEmpty() && sb.last().toString() == NUM_POINTS && content == NUM_POINTS
    }

    fun isLastNumPlus(content: String): Boolean {
        return sb.isNotEmpty() && sb.last().toString() == NUM_PLUS && content == NUM_PLUS
    }

    fun hasPointInCurrentNumber(content: String): Boolean {
        if (content != NUM_POINTS) return false

        val expr = sb.toString().substringAfterLast('\n')

        val afterEqual = expr.substringAfterLast('=', expr)

        val lastPlusIndex = afterEqual.lastIndexOf(NUM_PLUS)
        val currentNumber =
            if (lastPlusIndex == -1) afterEqual else afterEqual.substring(lastPlusIndex + 1)

        return currentNumber.contains(NUM_POINTS)
    }
}
