package com.github.crayonxiaoxin.ppjoke_kt.ui.view

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import com.github.crayonxiaoxin.lib_common.utils.dp
import com.github.crayonxiaoxin.ppjoke_kt.R
import com.github.crayonxiaoxin.ppjoke_kt.model.BottomBar
import com.github.crayonxiaoxin.ppjoke_kt.utils.AppConfig
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView

class AppBottomBar : BottomNavigationView {

    private val icons = arrayOf(
        R.drawable.icon_tab_home,
        R.drawable.icon_tab_sofa,
        R.drawable.icon_tab_publish,
        R.drawable.icon_tab_find,
        R.drawable.icon_tab_mine
    )
    private var config: BottomBar = AppConfig.getBottomBarConfig()

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attrs,
        defStyleAttr,
        0
    )

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {

        val state = arrayOf(intArrayOf(android.R.attr.state_selected), intArrayOf())
        val colors =
            intArrayOf(Color.parseColor(config.activeColor), Color.parseColor(config.inActiveColor))
        val colorStateList = ColorStateList(state, colors)

        itemTextColor = colorStateList
        itemIconTintList = colorStateList
        labelVisibilityMode = LABEL_VISIBILITY_LABELED
        selectedItemId = config.selectTab

        val tabs = config.tabs
        tabs.filter { it.enable }.forEach { tab ->
            val itemId = getItemId(tab.pageUrl)
            if (itemId >= 0) {
                val add = menu.add(0, itemId, tab.index, tab.title)
                add.setIcon(icons[tab.index])
            }
        }
        tabs.filter { it.enable }.forEachIndexed { index, tab ->
            val itemId = getItemId(tab.pageUrl)
            if (itemId >= 0) {
                val iconSize = tab.size.dp
                val menuView = getChildAt(0) as BottomNavigationMenuView
                val itemView = menuView.getChildAt(index) as BottomNavigationItemView
                itemView.setIconSize(iconSize)
                if (tab.title.isEmpty()) {
                    val tintColor = if (tab.tintColor.isNullOrEmpty()) {
                        Color.parseColor("#ff678f")
                    } else {
                        Color.parseColor(tab.tintColor)
                    }
                    itemView.setIconTintList(ColorStateList.valueOf(tintColor))
                    itemView.setShifting(false)
                }
            }
        }
    }

    private fun getItemId(pageUrl: String): Int {
        val destination = AppConfig.getDestConfig()[pageUrl]
        return destination?.id ?: -1
    }
}