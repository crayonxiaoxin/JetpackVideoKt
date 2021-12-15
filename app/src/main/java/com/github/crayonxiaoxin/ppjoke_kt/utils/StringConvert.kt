package com.github.crayonxiaoxin.ppjoke_kt.utils

import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan

object StringConvert {
    @JvmStatic
    fun convertFeedUgc(count: Int): String? {
        return if (count < 10000) {
            count.toString()
        } else (count / 10000).toString() + "万"
    }

    @JvmStatic
    fun convertTagFeedList(count: Int): String? {
        return if (count < 10000) {
            count.toString() + "人观看"
        } else (count / 10000).toString() + "万人观看"
    }

    @JvmStatic
    fun convertSpannable(count: Int, desc: String): String? {
        val countStr = count.toString()
        val ss = SpannableString(countStr + desc)
        ss.setSpan(
            ForegroundColorSpan(Color.BLACK),
            0,
            countStr.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        ss.setSpan(
            AbsoluteSizeSpan(16),
            0,
            countStr.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        ss.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            countStr.length,
            SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return ss.toString()
    }
}