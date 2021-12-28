package com.github.crayonxiaoxin.ppjoke_kt.utils

import java.util.*

object TimeUtils {
    @JvmStatic
    fun calculate(time: Long?): String {
        if (time == null) return "刚刚"
        val timeInMillis = Calendar.getInstance().timeInMillis
        val diffSecond = (timeInMillis - time) / 1000
        return when {
            diffSecond < 60 -> {
                "${diffSecond}秒前"
            }
            diffSecond < 3600 -> {
                "${diffSecond / 60}分钟前"
            }
            diffSecond < 24 * 3600 -> {
                "${diffSecond / 3600}小时前"
            }
            else -> {
                "${diffSecond / (24 * 3600)}天前"
            }
        }
    }
}