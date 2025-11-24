package com.cloudpos.k1demo.util;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.util.Arrays;
import java.util.List;

public class JTextViewUtil {
    private static final float MAX_TEXT_SIZE = 29F;

    private final TextView tvContent;
    private final TextView tvResult;

    public JTextViewUtil(TextView tvContent, TextView tvResult) {
        this.tvContent = tvContent;
        this.tvResult = tvResult;
    }

    public void moveScroller() {
        int scrollAmount = 0;
        if (tvContent.getLayout() != null) {
            scrollAmount = tvContent.getLayout().getLineTop(tvContent.getLineCount()) - tvContent.getHeight();
        }

        if (scrollAmount > 0) {
            tvContent.scrollTo(0, scrollAmount + 2);
        } else {
            tvContent.scrollTo(0, 0);
        }
    }

    public void scroller0() {
        tvContent.scrollTo(0, 0);
    }

    public void setContentText(Object any) {
        tvContent.setText(any != null ? any.toString() : "0");
    }

    public void setResultText(Object any) {
        tvResult.setText(any != null ? any.toString() : "0");
    }

    public void setContentTextSize(float size) {
        tvContent.setTextSize(size);
    }

    public void setResultTextSize(float size) {
        tvResult.setTextSize(size);
    }

    public void setContentTextColorGray() {
        tvContent.setTextColor(Color.GRAY);
    }

    public void setContentTextColorBlack() {
        tvContent.setTextColor(Color.BLACK);
    }

    public void setResultTextColorGray() {
        tvResult.setTextColor(Color.GRAY);
        tvResult.setTypeface(Typeface.DEFAULT);
    }

    public void setResultTextColorBlack() {
        tvResult.setTextColor(Color.BLACK);
        tvResult.setTypeface(Typeface.DEFAULT_BOLD);
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setContentTextSizeWithInput(StringBuilder sb) {
        long count = sb.chars().filter(ch -> ch == '\n').count();

        if (count == 0) {
            int len = sb.length();
            if (len >= 38) {
                abandonLongText(sb, 6);
                setContentTextSize(8F);
            } else if (len >= 25) {
                abandonLongText(sb, 5);
                setContentTextSize(12F);
            } else if (len >= 20) {
                abandonLongText(sb, 4);
                setContentTextSize(16F);
            } else if (len >= 15) {
                abandonLongText(sb, 3);
                setContentTextSize(20F);
            } else {
                abandonLongText(sb, 2);
                setContentTextSize(MAX_TEXT_SIZE);
            }
        } else {
            adjustTextSizeByContent(sb);
        }
    }


    public void adjustTextSizeByContent(StringBuilder sb) {
        String text = sb.toString();
        List<String> lines = Arrays.asList(text.split("\n"));

        String currentLine = "";
        int maxLength = 0;
        for (String line : lines) {
            int len = line.trim().length();
            if (len > maxLength) {
                maxLength = len;
                currentLine = line.trim();
            }
        }

        if (currentLine.isEmpty()) return;

        int length = currentLine.length();

        if (length >= 38) {
            abandonLongText(sb, 6);
            setContentTextSize(8F);
        } else if (length >= 25) {
            abandonLongText(sb, 5);
            setContentTextSize(12F);
        } else if (length >= 20) {
            abandonLongText(sb, 4);
            setContentTextSize(16F);
        } else if (length >= 15) {
            abandonLongText(sb, 3);
            setContentTextSize(20F);
        } else {
            abandonLongText(sb, 2);
        }
    }

    public void abandonLongText(StringBuilder sb, int maxLines) {
        String text = sb.toString();
        String[] lines = text.split("\n");
        if (lines.length > maxLines) {
            StringBuilder newText = new StringBuilder();
            for (int i = lines.length - maxLines; i < lines.length; i++) {
                newText.append(lines[i]);
                if (i < lines.length - 1) {
                    newText.append("\n");
                }
            }
            sb.setLength(0);
            sb.append(newText.toString());
        }
    }

    public void setTextSizeWithResult(String calculateResult) {
        if (calculateResult.length() > 12) {
            setResultTextSize(18F);
        } else {
            setResultTextSize(MAX_TEXT_SIZE);
        }
    }


    public void textGrayForLastResult(int start, int end) {
        String text = tvContent.getText().toString();
        if (text.isEmpty()) return;

        SpannableString spannable = new SpannableString(text);

        int safeStart = Math.max(0, Math.min(start, text.length()));
        int safeEnd = Math.max(safeStart, Math.min(end, text.length()));

        spannable.setSpan(
                new ForegroundColorSpan(Color.BLACK),
                0,
                text.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        spannable.setSpan(
                new ForegroundColorSpan(Color.GRAY),
                safeStart,
                safeEnd,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        tvContent.setText(spannable);
    }
}
