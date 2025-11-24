package com.cloudpos.k1demo.constants;


public class JCalcConstants {
    public static final String NUM_0 = "0";
    public static final String NUM_1 = "1";
    public static final String NUM_2 = "2";
    public static final String NUM_3 = "3";
    public static final String NUM_4 = "4";
    public static final String NUM_5 = "5";
    public static final String NUM_6 = "6";
    public static final String NUM_7 = "7";
    public static final String NUM_8 = "8";
    public static final String NUM_9 = "9";
    public static final String NUM_POINTS = ".";
    public static final String NUM_PLUS = "+";


    public static boolean isLegalNum(String content) {
        return NUM_0.equals(content)
                || NUM_1.equals(content)
                || NUM_2.equals(content)
                || NUM_3.equals(content)
                || NUM_4.equals(content)
                || NUM_5.equals(content)
                || NUM_6.equals(content)
                || NUM_7.equals(content)
                || NUM_8.equals(content)
                || NUM_9.equals(content)
                || NUM_POINTS.equals(content)
                || NUM_PLUS.equals(content);
    }
}
