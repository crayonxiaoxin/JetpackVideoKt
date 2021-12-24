package com.github.crayonxiaoxin.ppjoke_kt.ui.detail

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.github.crayonxiaoxin.lib_common.utils.dp
import com.github.crayonxiaoxin.ppjoke_kt.R

class ViewAnchorBehavior : CoordinatorLayout.Behavior<View> {

    private var anchorId: Int = 0
    private val extraUsed: Int = 48.dp

    constructor(anchorId: Int) {
        this.anchorId = anchorId
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.view_anchor_behavior)
        anchorId = typedArray.getResourceId(R.styleable.view_anchor_behavior_anchorId, 0)
        typedArray.recycle()
    }

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: View,
        dependency: View
    ): Boolean {
        return anchorId == dependency.id
    }

    override fun onMeasureChild(
        parent: CoordinatorLayout,
        child: View,
        parentWidthMeasureSpec: Int,
        widthUsed: Int,
        parentHeightMeasureSpec: Int,
        heightUsed: Int
    ): Boolean {
        val anchorView: View = parent.findViewById(anchorId) ?: return false
        val anchorBottom = anchorView.bottom
        val layoutParams = child.layoutParams as CoordinatorLayout.LayoutParams
        val topMargin = layoutParams.topMargin
        val newHeightUsed = anchorBottom + topMargin + extraUsed
        parent.onMeasureChild(
            child,
            parentWidthMeasureSpec,
            widthUsed,
            parentHeightMeasureSpec,
            newHeightUsed
        )
        return true
    }

    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: View,
        layoutDirection: Int
    ): Boolean {
        val anchorView: View = parent.findViewById(anchorId) ?: return false
        val anchorBottom = anchorView.bottom
        val layoutParams = child.layoutParams as CoordinatorLayout.LayoutParams
        val topMargin = layoutParams.topMargin
        parent.onLayoutChild(child, layoutDirection)
        child.offsetTopAndBottom(anchorBottom + topMargin)
        return true
    }
}