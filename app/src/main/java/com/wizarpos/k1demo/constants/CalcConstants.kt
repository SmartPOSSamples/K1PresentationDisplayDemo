package com.wizarpos.k1demo.constants

const val NUM_0 = "0"
const val NUM_1 = "1"
const val NUM_2 = "2"
const val NUM_3 = "3"
const val NUM_4 = "4"
const val NUM_5 = "5"
const val NUM_6 = "6"
const val NUM_7 = "7"
const val NUM_8 = "8"
const val NUM_9 = "9"
const val NUM_POINTS = "."
const val NUM_PLUS = "+"

fun isLegalNum(content: String): Boolean {
    return content == NUM_0 || content == NUM_1
            || content == NUM_2 || content == NUM_3
            || content == NUM_4 || content == NUM_5
            || content == NUM_6 || content == NUM_7
            || content == NUM_8 || content == NUM_9
            || content == NUM_POINTS || content == NUM_PLUS
}

