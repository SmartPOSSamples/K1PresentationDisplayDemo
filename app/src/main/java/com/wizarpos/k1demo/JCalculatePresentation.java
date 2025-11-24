package com.wizarpos.k1demo;

import static com.wizarpos.k1demo.constants.JCalcConstants.*;

import android.app.Presentation;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Display;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.wizarpos.k1demo.databinding.CalculatePresentationBinding;
import com.wizarpos.k1demo.util.JCalculateUtil;
import com.wizarpos.k1demo.util.JTextViewUtil;

import java.util.regex.Pattern;

public class JCalculatePresentation extends Presentation {
    private CalculatePresentationBinding binding; 

    private final StringBuilder sb = new StringBuilder();
    private String calculateResult = "0";
    private JTextViewUtil tvUtil;
    private JCalculateUtil calcUtil;

    private boolean clickResult = false;
    private boolean clickCancel = false;
    private boolean cancelOnce = true;
    private boolean deleteFlag = false;

    private static final int MAX_POINT_AFTER_NUM = 2;
    private static final int MAX_HUNDRED_BILLION = 12;

    public JCalculatePresentation(Context outerContext, Display display) {
        super(outerContext, display);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CalculatePresentationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.tvContent.setMovementMethod(ScrollingMovementMethod.getInstance());
        tvUtil = new JTextViewUtil(binding.tvContent, binding.tvResult);
        calcUtil = new JCalculateUtil(sb);
        tvUtil.setContentText(0);
        tvUtil.setResultText(0);
    }

    /** Calculation content */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void appendContent(String content) {
        if (isLegalNum(content)) {
            String fullText = sb.toString();
            String currentLine = fullText.substring(fullText.lastIndexOf('\n') + 1);
            String[] parts = currentLine.split(Pattern.quote(NUM_PLUS), -1);
            String lastNumberStr = parts.length > 0 ? parts[parts.length - 1].trim() : "";

            //Up to two decimal places
            if (!clickResult && !content.equals(NUM_PLUS) && !content.equals(NUM_POINTS)) {
                if (lastNumberStr.contains(NUM_POINTS)) {
                    String decimalPart = lastNumberStr.substring(lastNumberStr.indexOf(NUM_POINTS) + 1);
                    if (decimalPart.length() >= MAX_POINT_AFTER_NUM) {
                        return;
                    }
                } else {
                    if (lastNumberStr.length() >= MAX_HUNDRED_BILLION) {
                        return;
                    }
                }
            }

            if (sb.length() == 0 && content.equals(NUM_0)) {
                return;
            }

            if (currentLine.equals(NUM_0)) {
                if (content.equals(NUM_0)) {
                    return;
                } else if (!content.equals(NUM_POINTS) && !content.equals(NUM_PLUS)) {
                    int startIndex = fullText.lastIndexOf('\n');
                    if (startIndex == -1) startIndex = 0;
                    else startIndex += 1;
                    sb.delete(startIndex, sb.length());
                }
            }

            tvUtil.setContentTextColorBlack();
            cancelOnce = true;
            deleteFlag = false;

            if (sb.length() == 0 && content.equals(NUM_POINTS)) {
                sb.append(NUM_0).append(NUM_POINTS);

            } else if (sb.length() == 0 && content.equals(NUM_PLUS)) {
                sb.append(NUM_0).append(NUM_PLUS);

            } else if (clickResult && sb.length() > 0 && content.equals(NUM_PLUS)) {
                sb.append("=").append(calculateResult).append("\n").append(calculateResult).append(NUM_PLUS);
                tvUtil.setContentTextSizeWithInput(sb);

            } else if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '\n' && clickCancel) {
                normalInput(content);

            } else if (clickResult && sb.length() > 0) {
                if (!calcUtil.hasPointInCurrentNumber(content)) {
                    sb.append("=").append(calculateResult).append("\n");
                    tvUtil.setContentTextSizeWithInput(sb);
                    sb.append(content);
                }

            } else if (calcUtil.isLastNumPoint(content) || calcUtil.isLastNumPlus(content)) {
                // Ignore duplicate entries
            } else {
                normalInput(content);
            }

            clickResult = false;
            tvUtil.setContentText(sb.toString());
            tvUtil.moveScroller();

            int lastNewline = sb.lastIndexOf("\n");
            if (lastNewline != -1) {
                tvUtil.textGrayForLastResult(0, lastNewline);
            }

            calculateResult = calcUtil.calcSum(sb);
            tvUtil.setResultTextColorGray();
            tvUtil.setTextSizeWithResult(calculateResult);
            tvUtil.setResultText("= " + calculateResult);
        }
    }

    private void normalInput(String content) {
        if (!calcUtil.hasPointInCurrentNumber(content)) {
            tvUtil.setContentTextSizeWithInput(sb);
            sb.append(content);
        }
    }

    /**Click X to reset to 0 */
    public void appendCancel() {
        clickResult = false;

        if (sb.indexOf("\n") != -1 && cancelOnce) {
            cancelOnce = false;
            clickCancel = true;
            int lastNewlineIndex = sb.lastIndexOf("\n");
            if (lastNewlineIndex != -1 && lastNewlineIndex < sb.length() - 1) {
                sb.delete(lastNewlineIndex, sb.length());
            }

            tvUtil.setContentText(sb);
            tvUtil.setResultText(0);
            tvUtil.setResultTextColorGray();
            tvUtil.setContentTextColorGray();
            sb.append("\n");

        } else {
            cancelOnce = true;
            sb.setLength(0);
            tvUtil.setContentText(0);
            tvUtil.setResultText(0);
            tvUtil.setContentTextSize(30F);
            tvUtil.setResultTextSize(30F);
            tvUtil.setContentTextColorBlack();
            tvUtil.setResultTextColorGray();
            tvUtil.scroller0();
        }
    }

    /**  Click < Delete the previous character*/
    public void appendDelete() {
        if (deleteFlag) return;
        if (clickResult) return;

        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
            tvUtil.setContentText(sb);
            calculateResult = calcUtil.calcSum(sb);

            if (calculateResult.isEmpty()) {
                tvUtil.setResultText(0);
                tvUtil.setContentTextColorGray();
                deleteFlag = true;
                return;
            }

            tvUtil.setTextSizeWithResult(calculateResult);
            tvUtil.setResultText("= " + calculateResult);
        }

        if (sb.length() == 0) {
            tvUtil.setContentText(0);
        }

        int lastNewline = sb.lastIndexOf("\n");
        if (lastNewline != -1) {
            tvUtil.textGrayForLastResult(0, lastNewline);
        }
    }

    /**  Result calculated at point O*/
    public void appendResult() {
        if (!clickResult) {
            calculateResult = calcUtil.calcSum(sb);
            clickResult = true;

            if (calculateResult.isEmpty()) return;

            tvUtil.textGrayForLastResult(0, sb.length());
            tvUtil.setResultTextColorBlack();
            tvUtil.setTextSizeWithResult(calculateResult);
            tvUtil.setResultText("= " + calculateResult);
        }
    }

    /** Fn  */
    public void fnAction(String content) {
        Toast.makeText(binding.tvContent.getContext(), "click fn " + content, Toast.LENGTH_SHORT).show();
    }

    /** volume */
    public void volumeDown() {
        Toast.makeText(binding.tvContent.getContext(), "click volume down", Toast.LENGTH_SHORT).show();
    }

    /** QRCode */
    public void qrCode() {
        Toast.makeText(binding.tvContent.getContext(), "click qrCode", Toast.LENGTH_SHORT).show();
    }
}
