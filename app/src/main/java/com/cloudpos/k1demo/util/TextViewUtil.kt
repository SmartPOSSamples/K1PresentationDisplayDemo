package com.cloudpos.k1demo.util

import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.widget.TextView
import java.lang.StringBuilder

class TextViewUtil(private val tvContent: TextView, private val tvResult: TextView) {

    private companion object {
        private const val MAX_TEXT_SIZE = 29F
    }

    fun moveScroller() {
        var scrollAmount = 0
        tvContent.layout?.let {
            scrollAmount = it.getLineTop(tvContent.lineCount) - tvContent.height
        }

        if (scrollAmount > 0) {
            tvContent.scrollTo(0, scrollAmount + 2)
        } else {
            tvContent.scrollTo(0, 0)
        }
    }

    fun scroller0() {
        tvContent.scrollTo(0, 0)
    }

    fun setContentText(any: Any?) {
        tvContent.text = any?.toString() ?: "0"
    }

    fun setResultText(any: Any?) {
        tvResult.text = any?.toString() ?: "0"
    }

    fun setContentTextSize(size: Float) {
        tvContent.textSize = size
    }

    fun setResultTextSize(size: Float) {
        tvResult.textSize = size
    }

    fun setContentTextColorGray() {
        tvContent.setTextColor(Color.GRAY)
    }

    fun setContentTextColorBlack() {
        tvContent.setTextColor(Color.BLACK)
    }

    fun setResultTextColorGray() {
        tvResult.setTextColor(Color.GRAY)
        tvResult.typeface = Typeface.DEFAULT
    }

    fun setResultTextColorBlack() {
        tvResult.setTextColor(Color.BLACK)
        tvResult.typeface = Typeface.DEFAULT_BOLD
    }


    fun setContentTextSizeWithInput(sb: StringBuilder) {
        val count = sb.count { it == '\n' }
        if (count == 0) {
            when {
                sb.length >= 38 -> {
                    abandonLongText(sb, 6)
                    setContentTextSize(8F)
                }

                sb.length >= 25 -> {
                    abandonLongText(sb, 5)
                    setContentTextSize(12F)
                }

                sb.length >= 20 -> {
                    Log.d("abandonLongText","20")
                    abandonLongText(sb, 4)
                    setContentTextSize(16F)
                }

                sb.length >= 15 -> {
                    Log.d("abandonLongText","15")
                    abandonLongText(sb, 3)
                    setContentTextSize(20F)
                }

                else -> {
                    Log.d("abandonLongText","else")
                    abandonLongText(sb, 2)
                    setContentTextSize(MAX_TEXT_SIZE)
                }
            }
        } else {
            adjustTextSizeByContent(sb)
        }
    }

    fun adjustTextSizeByContent(sb: StringBuilder) {
        val lines = sb.toString().split('\n')

        val currentLine = lines.maxByOrNull { it.length }?.trim() ?: return

        val length = currentLine.length

        when {
            length >= 38 -> {
                abandonLongText(sb, 6)
                setContentTextSize(8F)
            }

            length >= 25 -> {
                abandonLongText(sb, 5)
                setContentTextSize(12F)
            }

            length >= 20 -> {
                abandonLongText(sb, 4)
                setContentTextSize(16F)
            }

            length >= 15 -> {
                abandonLongText(sb, 3)
                setContentTextSize(20F)
            }

            else -> {
                abandonLongText(sb, 2)
            }
        }
    }

    fun abandonLongText(sb: StringBuilder, maxLines: Int) {
        val text = sb.toString()
        val lines = text.split("\n")
        if (lines.size > maxLines) {
            val newText = lines.takeLast(maxLines).joinToString("\n")
            sb.clear()
            sb.append(newText)
        }
    }


    fun setTextSizeWithResult(calculateResult: String) {
        when {
            calculateResult.length > 12 -> setResultTextSize(18F)
            else -> setResultTextSize(MAX_TEXT_SIZE)
        }
    }


    fun textGrayForLastResult(start: Int, end: Int) {
        val text = tvContent.text.toString()
        if (text.isEmpty()) return

        val spannable = SpannableString(text)

        val safeStart = start.coerceIn(0, text.length)
        val safeEnd = end.coerceIn(safeStart, text.length)

        spannable.setSpan(
            ForegroundColorSpan(Color.BLACK),
            0,
            text.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        spannable.setSpan(
            ForegroundColorSpan(Color.GRAY),
            safeStart,
            safeEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        tvContent.text = spannable
    }
}
