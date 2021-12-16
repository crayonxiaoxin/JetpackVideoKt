package com.github.crayonxiaoxin.ppjoke_kt.exoplayer

import android.graphics.Point
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.github.crayonxiaoxin.ppjoke_kt.ui.view.ListPlayerView

class PageListPlayDetector(val lifecycleOwner: LifecycleOwner, val recyclerView: RecyclerView) {
    private var mTargets: MutableList<IPlayTarget> = ArrayList()
    private var playingTarget: IPlayTarget? = null
    private var rvLocation: Point? = null

    private val mAdapterDataObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
            postAutoPlay()
        }
    }

    private val mScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                postAutoPlay()
            }
        }

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            if (dx == 0 && dy == 0) {
                postAutoPlay()
            } else {
                if (playingTarget != null && playingTarget!!.isPlaying() && !isTargetBounds(
                        playingTarget
                    )
                ) {
                    playingTarget!!.inActive()
                }
            }
        }
    }

    init {
        lifecycleOwner.lifecycle.addObserver(object : LifecycleEventObserver {
            override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                if (event == Lifecycle.Event.ON_DESTROY) {
                    playingTarget = null
                    mTargets.clear()
                    recyclerView.removeCallbacks(runnable)
                    recyclerView.removeOnScrollListener(mScrollListener)
                    lifecycleOwner.lifecycle.removeObserver(this)
                }
            }
        })
        recyclerView.adapter?.registerAdapterDataObserver(mAdapterDataObserver)
        recyclerView.addOnScrollListener(mScrollListener)
    }

    private fun isTargetBounds(target: IPlayTarget?): Boolean {
        ensureRecyclerViewLocation()
        if (target == null) return false
        val owner = target.getOwner()
        if (!owner.isShown || !owner.isAttachedToWindow) return false

        val location = IntArray(2)
        owner.getLocationOnScreen(location)

        val center = location[1] + owner.height / 2

        return center >= rvLocation!!.x && center <= rvLocation!!.y
    }

    private fun ensureRecyclerViewLocation(): Point {
        if (rvLocation == null) {
            val location = IntArray(2)
            recyclerView.getLocationOnScreen(location)
            val top = location[1]
            rvLocation = Point(top, top + recyclerView.height)
        }
        return rvLocation!!
    }

    private val runnable = Runnable { autoPlay() }

    private fun postAutoPlay() {
        recyclerView.post(runnable)
    }

    private fun autoPlay() {
        if (mTargets.size <= 0 || recyclerView.childCount <= 0) {
            return
        }
        if (playingTarget != null && playingTarget!!.isPlaying() && isTargetBounds(playingTarget)) {
            return
        }
        var activeTarget: IPlayTarget? = null
        mTargets.forEach {
            if (isTargetBounds(it)) {
                activeTarget = it
                return@forEach
            }
        }
        activeTarget?.let {
            if (playingTarget != null && playingTarget!!.isPlaying()) {
                playingTarget!!.inActive()
            }
            playingTarget = it
            playingTarget!!.onActive()
        }
    }

    fun addTarget(listPlayerView: ListPlayerView?) {
        listPlayerView?.let {
            mTargets.add(it)
        }
    }

    fun removeTarget(listPlayerView: ListPlayerView?) {
        listPlayerView?.let {
            mTargets.remove(it)
        }
    }

    fun onResume() {
        playingTarget?.onActive()
    }

    fun onPause() {
        playingTarget?.inActive()
    }


}