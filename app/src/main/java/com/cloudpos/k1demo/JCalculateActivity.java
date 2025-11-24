package com.cloudpos.k1demo;

import static com.cloudpos.k1demo.constants.JCalcConstants.*;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;

import androidx.activity.ComponentActivity;
import androidx.annotation.RequiresApi;

import com.cloudpos.k1demo.databinding.ActivityPresentationBinding;

public class JCalculateActivity extends ComponentActivity {

    private ActivityPresentationBinding binding;
    private JCalculatePresentation presentation;
    private boolean clickFn = false;
    private Handler handler;

    public static final int RESET_FN = 0x123;
    public static final long RESET_FN_DURATION = 2000L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPresentationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        handler = new Handler(Looper.getMainLooper(), msg -> {
            if (msg.what == RESET_FN) {
                clickFn = false;
            } else {
                return false;
            }
            return true;
        });

        binding.btnShow.postDelayed(this::showPresentation, 1000);

        binding.btnShow.setOnClickListener(v -> {
            showPresentation();
        });

        binding.btnDismiss.setOnClickListener(v -> {
            dismissPresentation();
        });
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d("dispatchKeyEvent", "dispatchKeyEvent " + event.getKeyCode());
        if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            return onKeyDown(KeyEvent.KEYCODE_ENTER, event);
        }
        return super.dispatchKeyEvent(event);
    }

    private void showPresentation() {
        DisplayManager displayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
        Display[] displays = displayManager.getDisplays();

        // Search for external display devices
        Display secondaryDisplay = null;
        for (Display display : displays) {
            if (display.getDisplayId() != Display.DEFAULT_DISPLAY) {
                secondaryDisplay = display;
                break;
            }
        }

        if (secondaryDisplay == null) {
            Log.d("qiy_tag", "No external display device was detected.");
            return;
        }

        //  Create and display Presentation
        if (presentation == null) {
            presentation = new JCalculatePresentation(this, secondaryDisplay);
            presentation.show();
        }
    }

    private void dismissPresentation() {
        if (presentation != null) {
            presentation.dismiss();
            presentation = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissPresentation();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d("qiy_tag", "keyCode: " + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_0:
                isClickFnAction(NUM_0, () -> {
                    if (presentation != null) presentation.appendContent(NUM_0);
                });
                break;

            case KeyEvent.KEYCODE_1:
                isClickFnAction(NUM_1, () -> {
                    if (presentation != null) presentation.appendContent(NUM_1);
                });
                break;

            case KeyEvent.KEYCODE_2:
                isClickFnAction(NUM_2, () -> {
                    if (presentation != null) presentation.appendContent(NUM_2);
                });
                break;

            case KeyEvent.KEYCODE_3:
                isClickFnAction(NUM_3, () -> {
                    if (presentation != null) presentation.appendContent(NUM_3);
                });
                break;

            case KeyEvent.KEYCODE_4:
                if (presentation != null) presentation.appendContent(NUM_4);
                break;

            case KeyEvent.KEYCODE_5:
                if (presentation != null) presentation.appendContent(NUM_5);
                break;

            case KeyEvent.KEYCODE_6:
                if (presentation != null) presentation.appendContent(NUM_6);
                break;

            case KeyEvent.KEYCODE_7:
                if (presentation != null) presentation.appendContent(NUM_7);
                break;

            case KeyEvent.KEYCODE_8:
                if (presentation != null) presentation.appendContent(NUM_8);
                break;

            case KeyEvent.KEYCODE_9:
                if (presentation != null) presentation.appendContent(NUM_9);
                break;

            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (presentation != null) presentation.volumeDown();
                break;

            case KeyEvent.KEYCODE_DEL:
                if (presentation != null) presentation.appendDelete();
                break;

            case KeyEvent.KEYCODE_PERIOD:
                if (presentation != null) presentation.appendContent(NUM_POINTS);
                break;

            case KeyEvent.KEYCODE_NUMPAD_ADD:
                if (presentation != null) presentation.appendContent(NUM_PLUS);
                break;

            case KeyEvent.KEYCODE_FUNCTION:
                clickFn = true;
                if (handler != null) {
                    Message msg = Message.obtain();
                    msg.what = RESET_FN;
                    handler.sendMessageDelayed(msg, RESET_FN_DURATION);
                }
                break;

            case KeyEvent.KEYCODE_ENTER:
                if (presentation != null) presentation.appendResult();
                break;

            case KeyEvent.KEYCODE_ESCAPE:
                if (presentation != null) presentation.appendCancel();
                break;

            case KeyEvent.KEYCODE_CAMERA:
                if (presentation != null) presentation.qrCode();
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void isClickFnAction(String content, Runnable noClickFn) {
        if (clickFn) {
            if (presentation != null) presentation.fnAction(content);
            clickFn = false;
            if (handler != null) handler.removeCallbacksAndMessages(null);
        } else {
            if (noClickFn != null) noClickFn.run();
        }
    }
}