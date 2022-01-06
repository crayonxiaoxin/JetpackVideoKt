package com.github.crayonxiaoxin.lib_common.utils

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat

object StatusBar {
    /**
     * @param decorFitsSystemWindows 状态栏是否覆盖内容，true=不覆盖
     * @param darkIcons 状态栏文字图标颜色，true=暗色
     * @param fullscreen 状态栏是否隐藏，true=隐藏
     */
    fun fitSystemBar(
        activity: Activity,
        decorFitsSystemWindows: Boolean = true,
        darkIcons: Boolean = false,
        fullscreen: Boolean = false
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return
        }
        val window = activity.window
        val decorView = window.decorView
        // false 状态栏覆盖在 window 之上，true 不会覆盖
        WindowCompat.setDecorFitsSystemWindows(window, decorFitsSystemWindows)
        // 绘制 状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.TRANSPARENT
        // 设置 状态栏文字图标 颜色
        val windowInsetsController = ViewCompat.getWindowInsetsController(decorView)
        if (windowInsetsController != null) {
            windowInsetsController.isAppearanceLightStatusBars = darkIcons
        } else {
            var visibility = decorView.systemUiVisibility
            visibility = if (darkIcons) {
                visibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            } else {
                visibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            }
            decorView.systemUiVisibility = visibility
        }
        // 设置 是否全屏
        if (fullscreen && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val attributes = window.attributes
            attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            window.attributes = attributes
        }
    }
}

fun Activity.fitSystemBar(
    decorFitsSystemWindows: Boolean = true,
    darkIcons: Boolean = false,
    fullscreen: Boolean = false
) {
    StatusBar.fitSystemBar(this, decorFitsSystemWindows, darkIcons, fullscreen)
}