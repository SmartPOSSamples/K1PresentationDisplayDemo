package com.wizarpos.k1demo

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.Display
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import com.wizarpos.k1demo.constants.NUM_0
import com.wizarpos.k1demo.constants.NUM_1
import com.wizarpos.k1demo.constants.NUM_2
import com.wizarpos.k1demo.constants.NUM_3
import com.wizarpos.k1demo.constants.NUM_4
import com.wizarpos.k1demo.constants.NUM_5
import com.wizarpos.k1demo.constants.NUM_6
import com.wizarpos.k1demo.constants.NUM_7
import com.wizarpos.k1demo.constants.NUM_8
import com.wizarpos.k1demo.constants.NUM_9
import com.wizarpos.k1demo.constants.NUM_PLUS
import com.wizarpos.k1demo.constants.NUM_POINTS
import com.wizarpos.k1demo.databinding.ActivityPresentationBinding

class CalculateActivity : ComponentActivity() {

    private lateinit var binding: ActivityPresentationBinding

    private var presentation: CalculatePresentation? = null

    private var clickFn = false

    private var handler: Handler? = null

    companion object {
        const val RESET_FN = 0x123
        const val RESET_FN_DURATION = 2000L
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPresentationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        handler = Handler(mainLooper) {
            when (it.what) {
                RESET_FN -> {
                    clickFn = false
                    Log.d("qiy_tag", "fn reset")
                }
                else -> return@Handler false
            }
            true
        }

        binding.apply {
            btnShow.postDelayed({
                showPresentation()
            }, 1000)
            btnShow.setOnClickListener {
                showPresentation()
                Log.d("qiy_tag", "onclick")
            }
            btnDismiss.setOnClickListener {
                dismissPresentation()
                Log.d("qiy_tag", "btnDismiss onclick")
            }
        }
    }

    @SuppressLint("RestrictedApi")
    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        Log.d("dispatchKeyEvent", "dispatchKeyEvent ${event.keyCode}")
        if (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER) {
            return onKeyDown(KeyEvent.KEYCODE_ENTER, event)
        }
        return super.dispatchKeyEvent(event)
    }

    private fun showPresentation() {
        val displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        val displays = displayManager.displays

        val secondaryDisplay = displays.firstOrNull { it.displayId != Display.DEFAULT_DISPLAY }
        if (secondaryDisplay == null) {
            Log.d("qiy_tag", "No external display device was detected.")
            return
        }
        val mode = secondaryDisplay.mode

        val width = mode.physicalWidth
        val height = mode.physicalHeight

        Log.d("qiy_tag", "Sub-display resolution：${width} x ${height}") //320*170

        if (presentation == null) {
            presentation = CalculatePresentation(this, secondaryDisplay)
            presentation?.show()
        }
    }

    private fun dismissPresentation() {
        presentation?.dismiss()
        presentation = null
    }

    override fun onDestroy() {
        super.onDestroy()
        dismissPresentation()
        handler?.removeCallbacksAndMessages(null)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        Log.d("qiy_tag", "keyCode: $keyCode")
        when (keyCode) {
            KeyEvent.KEYCODE_0 -> {
                isClickFnAction(NUM_0) {
                    presentation?.appendContent(NUM_0)
                }
            }

            KeyEvent.KEYCODE_1 -> {
                isClickFnAction(NUM_1) {
                    presentation?.appendContent(NUM_1)
                }
            }

            KeyEvent.KEYCODE_2 -> {
                isClickFnAction(NUM_2) {
                    presentation?.appendContent(NUM_2)
                }
            }

            KeyEvent.KEYCODE_3 -> {
                isClickFnAction(NUM_3) {
                    presentation?.appendContent(NUM_3)
                }
            }

            KeyEvent.KEYCODE_4 -> {
                presentation?.appendContent(NUM_4)
            }

            KeyEvent.KEYCODE_5 -> {
                presentation?.appendContent(NUM_5)
            }

            KeyEvent.KEYCODE_6 -> {
                presentation?.appendContent(NUM_6)
            }

            KeyEvent.KEYCODE_7 -> {
                presentation?.appendContent(NUM_7)
            }

            KeyEvent.KEYCODE_8 -> {
                presentation?.appendContent(NUM_8)
            }

            KeyEvent.KEYCODE_9 -> {
                presentation?.appendContent(NUM_9)
            }

            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                presentation?.volumeDown()
            }

            KeyEvent.KEYCODE_DEL -> {
                presentation?.appendDelete()
            }

            KeyEvent.KEYCODE_PERIOD -> {// .
                presentation?.appendContent(NUM_POINTS)
            }

            KeyEvent.KEYCODE_NUMPAD_ADD -> {// +
                presentation?.appendContent(NUM_PLUS)
            }

            KeyEvent.KEYCODE_FUNCTION -> {// FN
                clickFn = true
                handler?.sendMessageDelayed(Message().apply {
                    what = RESET_FN
                }, RESET_FN_DURATION)
            }

            KeyEvent.KEYCODE_ENTER -> {// O
                presentation?.appendResult()
            }

            KeyEvent.KEYCODE_ESCAPE -> {// X
                presentation?.appendCancel()
            }

            KeyEvent.KEYCODE_CAMERA -> {// qr
                presentation?.qrCode()
            }

        }
        return super.onKeyDown(keyCode, event)
    }

    private fun isClickFnAction(content: String, noClickFn: (() -> Unit)? = null) {
        if (clickFn) {
            presentation?.fnAction(content)
            clickFn = false
            handler?.removeCallbacksAndMessages(null)
        } else {
            noClickFn?.invoke()
        }
    }

}