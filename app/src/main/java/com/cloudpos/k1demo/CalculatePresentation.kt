package com.cloudpos.k1demo

import android.app.Presentation
import android.content.Context
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.Display
import android.widget.Toast
import com.cloudpos.k1demo.constants.*
import com.cloudpos.k1demo.constants.isLegalNum
import com.cloudpos.k1demo.databinding.CalculatePresentationBinding
import com.cloudpos.k1demo.util.CalculateUtil
import com.cloudpos.k1demo.util.TextViewUtil


class CalculatePresentation(context: Context, display: Display) : Presentation(context, display) {
    private lateinit var binding: CalculatePresentationBinding

    private val sb: StringBuilder = StringBuilder()
    private var calculateResult: String = "0"
    private lateinit var tvUtil: TextViewUtil
    private lateinit var calcUtil: CalculateUtil
    private var clickResult = false //After clicking the Enter button, (1. Once you click "OK" to get the result, you cannot delete the previous input. 2. You cannot click continuously.)
    private var clickCancel = false
    private var cancelOnce = true
    private var deleteFlag = false

    companion object{
        private const val MAX_POINT_AFTER_NUM = 2
        private const val MAX_HUNDRED_BILLION = 12
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CalculatePresentationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tvContent.movementMethod = ScrollingMovementMethod.getInstance()
        tvUtil = TextViewUtil(binding.tvContent, binding.tvResult)
        calcUtil = CalculateUtil(sb)
        tvUtil.setContentText(0)
        tvUtil.setResultText(0)
    }

    fun appendContent(content: String) {
        if (isLegalNum(content)) {
            val currentLine = sb.toString().substringAfterLast('\n')
            val lastNumberStr = currentLine.split(NUM_PLUS).lastOrNull()?.trim() ?: ""

            Log.d("lastNumberStr","${lastNumberStr}_lastNumberStr:${lastNumberStr.length}")

            if (!clickResult && content != NUM_PLUS && content != NUM_POINTS) {
                if (lastNumberStr.contains(NUM_POINTS)) {
                    val decimalPart = lastNumberStr.substringAfter(NUM_POINTS)
                    if (decimalPart.length >= MAX_POINT_AFTER_NUM) {
                        return //The decimal point has reached two decimal places. No more digits can be entered.
                    }
                } else {
                    if (lastNumberStr.length >= MAX_HUNDRED_BILLION) {
                        return//Up to 12 digits before the decimal point, quadrillion
                    }
                }
            }

            // Situation 1: Initial state (sb is empty) and input 0 -> No change
            if (sb.isEmpty() && content == NUM_0) {
                return
            }

            // Situation 2: The current row contains only one "0"
            if (currentLine == NUM_0) {
                if (content == NUM_0) {
                    return
                } else if (content != NUM_POINTS && content != NUM_PLUS) {
                    val startIndex = sb.lastIndexOf('\n').let { if (it == -1) 0 else it + 1 }
                    sb.delete(startIndex, sb.length)
                }
            }

            tvUtil.setContentTextColorBlack()
            cancelOnce = true
            deleteFlag = false
            when {
                sb.isEmpty() && content == NUM_POINTS -> {
                    sb.append(NUM_0)
                    sb.append(NUM_POINTS)
                }

                sb.isEmpty() && content == NUM_PLUS -> {
                    sb.append(NUM_0)
                    sb.append(NUM_PLUS)
                }

                clickResult && sb.isNotEmpty() && content == NUM_PLUS -> {
                    sb.append("=$calculateResult")
                    sb.append("\n")
                    sb.append(calculateResult)
                    sb.append(NUM_PLUS)
                    tvUtil.setContentTextSizeWithInput(sb)
                }

                sb.isNotEmpty() && sb.last().toString() == "\n" && clickCancel -> {
                    normalInput(content)
                }

                clickResult && sb.isNotEmpty() -> {
                    if (calcUtil.hasPointInCurrentNumber(content).not()) {
                        sb.append("=$calculateResult")
                        sb.append("\n")
                        tvUtil.setContentTextSizeWithInput(sb)
                        sb.append(content)
                    }
                }

                calcUtil.isLastNumPoint(content) || calcUtil.isLastNumPlus(content) -> {

                }

                else -> {
                    normalInput(content)
                }
            }

            clickResult = false
            tvUtil.setContentText(sb.toString())
            tvUtil.moveScroller()

            val lastNewline = sb.lastIndexOf('\n')
            if (lastNewline != -1) {
                tvUtil.textGrayForLastResult(0, lastNewline)
            }

            calculateResult = calcUtil.calcSum(sb)
            tvUtil.setResultTextColorGray()
            tvUtil.setTextSizeWithResult(calculateResult)
            tvUtil.setResultText("= $calculateResult")
        }
    }

    private fun normalInput(content: String) {
        if (calcUtil.hasPointInCurrentNumber(content).not()) {
            tvUtil.setContentTextSizeWithInput(sb)
            sb.append(content)
        }
    }

    fun appendCancel() {
        clickResult = false
        if (sb.contains("\n") && cancelOnce) {
            cancelOnce = false
            clickCancel = true
            val lastNewlineIndex = sb.lastIndexOf('\n')
            if (lastNewlineIndex != -1 && lastNewlineIndex < sb.length - 1) {
                sb.delete(lastNewlineIndex, sb.length)
            }

            tvUtil.apply {
                setContentText(sb)
                setResultText(0)
                setResultTextColorGray()
                setContentTextColorGray()
            }
            sb.append("\n")
        } else {
            cancelOnce = true
            sb.clear()
            tvUtil.apply {
                setContentText(0)
                setResultText(0)
                setContentTextSize(30F)
                setResultTextSize(30F)
                setContentTextColorBlack()
                setResultTextColorGray()
                scroller0()
            }
        }
    }

    fun appendDelete() {
        if (deleteFlag) return
        if (clickResult) return
        tvUtil.apply {
            if (sb.isNotEmpty()) {
                sb.deleteCharAt(sb.lastIndex)
                setContentText(sb)
                calculateResult = calcUtil.calcSum(sb)
                if (calculateResult.isEmpty()) {
                    setResultText(0)
                    setContentTextColorGray()
                    deleteFlag = true
                    return
                }
                setTextSizeWithResult(calculateResult)
                setResultText("= $calculateResult")
            }
            if (sb.isEmpty()) {
                setContentText(0)
            }
            val lastNewline = sb.lastIndexOf('\n')
            if (lastNewline != -1) {
                textGrayForLastResult(0, lastNewline)
            }
        }
    }

    fun appendResult() {
        if (clickResult.not()) {
            calculateResult = calcUtil.calcSum(sb)
            clickResult = true
            if (calculateResult.isEmpty()) return
            tvUtil.textGrayForLastResult(0, sb.length)
            tvUtil.setResultTextColorBlack()
            tvUtil.setTextSizeWithResult(calculateResult)
            tvUtil.setResultText("= $calculateResult")
        }
    }

    //Fn 1
    fun fnAction(content: String) {
        Toast.makeText(this.context, "click fn $content", Toast.LENGTH_SHORT).show()
    }

    fun volumeDown() {
        Toast.makeText(this.context, "click volume down", Toast.LENGTH_SHORT).show()
    }

    //qrcode
    fun qrCode() {
        Toast.makeText(this.context, "click qrCode", Toast.LENGTH_SHORT).show()
    }

}