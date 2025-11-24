package com.wizarpos.k1demo.util;



import static com.wizarpos.k1demo.constants.CalcConstantsKt.NUM_PLUS;
import static com.wizarpos.k1demo.constants.CalcConstantsKt.NUM_POINTS;

import java.math.BigDecimal;

public class JCalculateUtil {

    private final StringBuilder sb;

    public JCalculateUtil(StringBuilder sb) {
        this.sb = sb;
    }

    public String calcSum(StringBuilder sb) {
        String expr = sb.toString();
        int lastNewLine = expr.lastIndexOf('\n');
        if (lastNewLine != -1) {
            expr = expr.substring(lastNewLine + 1);
        }

        int equalIndex = expr.indexOf('=');
        if (equalIndex != -1) {
            expr = expr.substring(0, equalIndex);
        }

        expr = expr.trim();
        if (expr.isEmpty()) {
            return "";
        }

        String[] parts = expr.split("\\+");
        BigDecimal sum = BigDecimal.ZERO;

        for (String part : parts) {
            if (!part.isEmpty()) {
                try {
                    BigDecimal value = new BigDecimal(part);
                    sum = sum.add(value);
                } catch (NumberFormatException e) {

                }
            }
        }

        BigDecimal result = sum.stripTrailingZeros();
        return result.toPlainString();
    }

    public boolean isLastNumPoint(String content) {
        if (sb.length() == 0) return false;
        char lastChar = sb.charAt(sb.length() - 1);
        return content.equals(NUM_POINTS) && lastChar == NUM_POINTS.charAt(0);
    }


    public boolean isLastNumPlus(String content) {
        if (sb.length() == 0) return false;
        char lastChar = sb.charAt(sb.length() - 1);
        return content.equals(NUM_PLUS) && lastChar == NUM_PLUS.charAt(0);
    }


    public boolean hasPointInCurrentNumber(String content) {
        if (!content.equals(NUM_POINTS)) {
            return false;
        }

        String expr = sb.toString();
        int lastNewLine = expr.lastIndexOf('\n');
        if (lastNewLine != -1) {
            expr = expr.substring(lastNewLine + 1);
        }

        int equalIndex = expr.lastIndexOf('=');
        String afterEqual = (equalIndex != -1) ? expr.substring(equalIndex + 1) : expr;

        int lastPlusIndex = afterEqual.lastIndexOf(NUM_PLUS.charAt(0));
        String currentNumber = (lastPlusIndex == -1)
                ? afterEqual
                : afterEqual.substring(lastPlusIndex + 1);

        return currentNumber.contains(NUM_POINTS);
    }
}
