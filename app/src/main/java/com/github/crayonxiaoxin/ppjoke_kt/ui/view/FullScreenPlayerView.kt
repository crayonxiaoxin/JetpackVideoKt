package com.github.crayonxiaoxin.ppjoke_kt.ui.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewParent
import android.widget.FrameLayout
import com.github.crayonxiaoxin.lib_common.utils.PixUtils
import com.github.crayonxiaoxin.ppjoke_kt.R
import com.github.crayonxiaoxin.ppjoke_kt.exoplayer.PageListPlayManager
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView

class FullScreenPlayerView : ListPlayerView {
    private var exoPlayerView: PlayerView? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context, attrs, defStyleAttr, 0
    )

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        exoPlayerView = LayoutInflater.from(context)
            .inflate(R.layout.layout_exo_player_view, null, false) as PlayerView?
    }

    override fun setSize() {
        if (mWidthPx >= mHeightPx) {
            super.setSize()
        } else {
            val maxWidth = PixUtils.getScreenWidth()
            val maxHeight = PixUtils.getScreenHeight()

            val layoutParams = this.layoutParams
            layoutParams.width = maxWidth
            layoutParams.height = maxHeight
            setLayoutParams(layoutParams)
            setBackgroundColor(Color.BLACK)

            val coverParams = coverView.layoutParams as LayoutParams
            coverParams.width = (mWidthPx / (mHeightPx * 1.0f / maxHeight)).toInt()
            coverParams.height = maxHeight
            coverParams.gravity = Gravity.CENTER
            coverView.layoutParams = coverParams
        }
    }

    override fun setLayoutParams(params: ViewGroup.LayoutParams?) {
        params?.let {
            if (mWidthPx < mHeightPx) { // 还没有滑动到最小高度，进行等比缩放
                val layoutWidth = params.width
                val layoutHeight = params.height

                // 封面 等比缩放
                val coverParams = coverView.layoutParams
                coverParams.width = (mWidthPx / (mHeightPx * 1.0f / layoutHeight)).toInt()
                coverParams.height = layoutHeight
                coverView.layoutParams = coverParams

                // 播放器 等比缩放
                exoPlayerView?.let { playerView ->
                    val layoutParams = playerView.layoutParams
                    if (layoutParams != null && layoutParams.width > 0 && layoutParams.height > 0) {
                        val scaleX = coverParams.width * 1.0f / layoutParams.width
                        val scaleY = coverParams.height * 1.0f / layoutParams.height
                        playerView.scaleX = scaleX
                        playerView.scaleY = scaleY
                    }
                }
            }
        }
        super.setLayoutParams(params)
    }

    override fun onActive() {
        val pageListPlay = PageListPlayManager.get(mCategory)
        val exoPlayer = pageListPlay.exoPlayer
        val playerView = exoPlayerView
        val controllerView = pageListPlay.controllerView
        if (playerView == null || exoPlayer == null) return

        pageListPlay.switchPlayerView(playerView, true)

        val parent: ViewParent? = playerView.parent
        if (parent != this) {
            if (parent != null) {
                (parent as ViewGroup).removeView(playerView)
                // 这里不用暂停播放，因为不是列表，且需要续播
//                (parent as ListPlayerView).inActive()
            }
            this.addView(playerView, 1, coverView.layoutParams)
        }

        if (controllerView != null) {
            val ctrlParent: ViewParent? = controllerView.parent
            if (ctrlParent != this) {
                if (ctrlParent != null) {
                    (ctrlParent as ViewGroup).removeView(controllerView)
                }
                val ctrlLayoutParams =
                    LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                ctrlLayoutParams.gravity = Gravity.BOTTOM
                this.addView(controllerView, ctrlLayoutParams)
            }
        }
        if (pageListPlay.playUrl == mVideoUrl) { // 继续
            onPlaybackStateChanged(Player.STATE_READY)
        } else { // 不是一个视频资源
            val mediaSource = PageListPlayManager.createMediaSource(mVideoUrl)
            exoPlayer.setMediaSource(mediaSource)
            exoPlayer.prepare()
            pageListPlay.playUrl = mVideoUrl
        }

        controllerView?.show()
        controllerView?.addVisibilityListener(this)

        exoPlayer.repeatMode = Player.REPEAT_MODE_ONE  // 重复播放这一个，每次播放都重新开启 loop，因为暂停的时候关闭了
        exoPlayer.addListener(this)
        exoPlayer.playWhenReady = true
    }

    override fun inActive() {
        super.inActive()
        val pageListPlay = PageListPlayManager.get(mCategory)
        // 停止详情页播放，切换回原来的播放器（即列表页的播放器）继续播放
        exoPlayerView?.let {
            pageListPlay.switchPlayerView(it, false)
        }
    }

}