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
            if (mWidthPx < mHeightPx) { // ???????????????????????????????????????????????????
                val layoutWidth = params.width
                val layoutHeight = params.height

                // ?????? ????????????
                val coverParams = coverView.layoutParams
                coverParams.width = (mWidthPx / (mHeightPx * 1.0f / layoutHeight)).toInt()
                coverParams.height = layoutHeight
                coverView.layoutParams = coverParams

                // ????????? ????????????
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
                // ???????????????????????????????????????????????????????????????
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
        if (pageListPlay.playUrl == mVideoUrl) { // ??????
            onPlaybackStateChanged(Player.STATE_READY)
        } else { // ????????????????????????
            val mediaSource = PageListPlayManager.createMediaSource(mVideoUrl)
            exoPlayer.setMediaSource(mediaSource)
            exoPlayer.prepare()
            pageListPlay.playUrl = mVideoUrl
        }

        controllerView?.show()
        controllerView?.addVisibilityListener(this)

        exoPlayer.repeatMode = Player.REPEAT_MODE_ONE  // ??????????????????????????????????????????????????? loop?????????????????????????????????
        exoPlayer.addListener(this)
        exoPlayer.playWhenReady = true
    }

    override fun inActive() {
        super.inActive()
        val pageListPlay = PageListPlayManager.get(mCategory)
        // ?????????????????????????????????????????????????????????????????????????????????????????????
        exoPlayerView?.let {
            pageListPlay.switchPlayerView(it, false)
        }
    }

}