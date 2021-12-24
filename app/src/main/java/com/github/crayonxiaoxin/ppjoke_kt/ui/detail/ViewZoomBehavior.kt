package com.github.crayonxiaoxin.ppjoke_kt.ui.detail

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.OverScroller
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import com.github.crayonxiaoxin.lib_common.utils.dp
import com.github.crayonxiaoxin.ppjoke_kt.R
import com.github.crayonxiaoxin.ppjoke_kt.ui.view.FullScreenPlayerView
import kotlin.math.max
import kotlin.math.min

class ViewZoomBehavior : CoordinatorLayout.Behavior<FullScreenPlayerView> {

    private var minHeight: Int = 0
    private var scrollingId: Int

    private var childOriginalHeight: Int = 0
    private var scrollingView: View? = null
    private var refChild: FullScreenPlayerView? = null
    private var canFullScreen: Boolean = false

    private var runnable: FlingRunnable? = null
    private var viewDragHelper: ViewDragHelper? = null
    private var overScroller: OverScroller
    private var mViewZoomCallback: ((height: Int) -> Unit)? = null

    constructor(context: Context, attrs: AttributeSet?) {
        val typedArray =
            context.obtainStyledAttributes(attrs, R.styleable.view_zoom_behavior)
        scrollingId = typedArray.getResourceId(R.styleable.view_zoom_behavior_scrolling_id, 0)
        minHeight =
            typedArray.getDimensionPixelOffset(R.styleable.view_zoom_behavior_min_height, 200.dp)
        typedArray.recycle()
        overScroller = OverScroller(context)
    }

    fun setOnViewZoomCallback(callback: (height: Int) -> Unit) {
        this.mViewZoomCallback = callback
    }

    override fun onLayoutChild(
        parent: CoordinatorLayout,
        child: FullScreenPlayerView,
        layoutDirection: Int
    ): Boolean {
        if (viewDragHelper == null) {
            viewDragHelper = ViewDragHelper.create(parent, 1.0f, mCallback)
            scrollingView = parent.findViewById(scrollingId)
            refChild = child
            childOriginalHeight = child.measuredHeight
            canFullScreen = childOriginalHeight > parent.measuredWidth
        }
        return super.onLayoutChild(parent, child, layoutDirection)
    }

    private val mCallback: ViewDragHelper.Callback = object : ViewDragHelper.Callback() {
        // 什么时候拦截手势
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            if (!canFullScreen) return false
            refChild?.let { playerView ->
                runnable?.let {
                    playerView.removeCallbacks(it)
                }
                val refBottom = playerView.bottom
                if (child == playerView) {
                    return refBottom in minHeight..childOriginalHeight
                }
                if (child == scrollingView) {
                    val canScrollUp = scrollingView?.canScrollVertically(-1) ?: false
                    if (canScrollUp) return false
                    return refBottom != minHeight && refBottom != childOriginalHeight
                }
            }
            return false
        }

        // 滑动多少距离 视为 拖拽
        override fun getViewVerticalDragRange(child: View): Int {
            return viewDragHelper?.touchSlop ?: 10
        }

        // 本次滑动最终能滑动的距离
        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            if (refChild == null || dy == 0) return 0
            // dy>0 向下滑，dy<0 向上滑
            val refBottom = refChild!!.bottom
            Log.e("TAG", "clampViewPositionVertical: ${scrollingView!!.canScrollVertically(-1)}")
            if ((dy < 0 && refBottom <= minHeight)
                || (dy > 0 && refBottom >= childOriginalHeight)
                || (dy > 0 && (scrollingView != null && scrollingView!!.canScrollVertically(-1)))
            ) {
                return 0
            }
            val maxConsumed = if (dy > 0) {
                if (refBottom + dy > childOriginalHeight) { // 下滑的高度不能超过原始高度
                    childOriginalHeight - refBottom
                } else {
                    dy
                }
            } else {
                if (refBottom + dy < minHeight) { // 上画的高度不能超过最小高度
                    minHeight - refBottom
                } else {
                    dy
                }
            }
            // playerView 等比缩放
            refChild?.let {
                val layoutParams = it.layoutParams
                layoutParams.height = layoutParams.height + maxConsumed
                it.layoutParams = layoutParams
                mViewZoomCallback?.invoke(layoutParams.height)
            }
            return maxConsumed
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            super.onViewReleased(releasedChild, xvel, yvel)
            refChild?.let {
                it.removeCallbacks(runnable)
                if (it.bottom > minHeight && it.bottom < childOriginalHeight && yvel != 0f) {
                    runnable = FlingRunnable(it).apply { fling(xvel.toInt(), yvel.toInt()) }
                }
            }
        }
    }

    override fun onTouchEvent(
        parent: CoordinatorLayout,
        child: FullScreenPlayerView,
        ev: MotionEvent
    ): Boolean {
        if (!canFullScreen || viewDragHelper == null) {
            return super.onTouchEvent(parent, child, ev)
        }
        viewDragHelper?.processTouchEvent(ev)
        return true
    }

    override fun onInterceptTouchEvent(
        parent: CoordinatorLayout,
        child: FullScreenPlayerView,
        ev: MotionEvent
    ): Boolean {
        if (!canFullScreen || viewDragHelper == null) {
            return super.onInterceptTouchEvent(parent, child, ev)
        }
        return viewDragHelper!!.shouldInterceptTouchEvent(ev)
    }

    private inner class FlingRunnable(val view: View) : Runnable {
        fun fling(xVel: Int, yVel: Int) {
            overScroller.fling(0, view.bottom, xVel, yVel, 0, Int.MAX_VALUE, 0, Int.MAX_VALUE)
            run()
        }

        override fun run() {
            val layoutParams = view.layoutParams
            val height = layoutParams.height
            if (overScroller.computeScrollOffset() && height >= minHeight && height <= childOriginalHeight) {
//                val newHeight = min(overScroller.currY, childOriginalHeight)
                // 不能超过原始值  不能少于最小值
                val newHeight = max(min(overScroller.currY, childOriginalHeight), minHeight)
                if (newHeight != height) {
                    layoutParams.height = newHeight
                    view.layoutParams = layoutParams
                    mViewZoomCallback?.invoke(newHeight)
                }
                ViewCompat.postOnAnimation(view, this)
            } else {
                view.removeCallbacks(this)
            }
        }
    }
}