package com.github.crayonxiaoxin.ppjoke_kt.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import com.github.crayonxiaoxin.lib_common.utils.PixUtils
import com.github.crayonxiaoxin.ppjoke_kt.R
import com.github.crayonxiaoxin.ppjoke_kt.exoplayer.IPlayTarget
import com.github.crayonxiaoxin.ppjoke_kt.exoplayer.PageListPlayManager
import com.github.crayonxiaoxin.ppjoke_kt.utils.setBlurImageUrl
import com.github.crayonxiaoxin.ppjoke_kt.utils.setImageUrl
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerControlView

class ListPlayerView : FrameLayout, IPlayTarget, Player.Listener,
    PlayerControlView.VisibilityListener {
    private var mVideoUrl: String = ""
    private var isPlaying: Boolean = false
    private var mCategory: String = ""
    private var mWidthPx: Int = 0
    private var mHeightPx: Int = 0

    private var blurView: PPImageView
    private var coverView: PPImageView
    private var playBtn: ImageView
    private var bufferView: ProgressBar

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
        LayoutInflater.from(context).inflate(R.layout.layout_player_view, this, true)
        blurView = findViewById(R.id.blur_background)
        coverView = findViewById(R.id.cover)
        playBtn = findViewById(R.id.play_btn)
        bufferView = findViewById(R.id.buffer_view)
        playBtn.setOnClickListener {
            if (isPlaying()) {
                inActive()
            } else {
                onActive()
            }
        }
    }

    fun bindData(
        category: String,
        widthPx: Int,
        heightPx: Int,
        coverUrl: String,
        videoUrl: String
    ) {
        mCategory = category
        mWidthPx = widthPx
        mHeightPx = heightPx
        mVideoUrl = videoUrl
        setImageUrl(coverView, coverUrl)
        if (widthPx < heightPx) {
            setBlurImageUrl(blurView, coverUrl, 10)
            blurView.visibility = VISIBLE
        } else {
            blurView.visibility = INVISIBLE
        }
        setSize()
    }

    private fun setSize() {
        val maxWidth = PixUtils.getScreenWidth()
        val maxHeight = maxWidth
        val layoutWidth = maxWidth
        var layoutHeight = 0
        var coverWidth = 0
        var coverHeight = 0
        if (mWidthPx >= mHeightPx) {
            coverWidth = maxWidth
            coverHeight = (mHeightPx / (mWidthPx * 1.0f / maxWidth)).toInt()
            layoutHeight = coverHeight
        } else {
            coverHeight = maxHeight
            layoutHeight = coverHeight
            coverWidth = (mWidthPx / (mHeightPx * 1.0f / maxHeight)).toInt()
        }
        val params = layoutParams
        params.width = layoutWidth
        params.height = layoutHeight
        layoutParams = params

        val blurLayoutParams = blurView.layoutParams
        blurLayoutParams.width = layoutWidth
        blurLayoutParams.height = layoutHeight
        blurView.layoutParams = blurLayoutParams

        val coverLayoutParams = coverView.layoutParams as LayoutParams
        coverLayoutParams.width = coverWidth
        coverLayoutParams.height = coverHeight
        coverLayoutParams.gravity = Gravity.CENTER
        coverView.layoutParams = coverLayoutParams

        val playBtnLayoutParams = playBtn.layoutParams as LayoutParams
        playBtnLayoutParams.gravity = Gravity.CENTER
        playBtn.layoutParams = playBtnLayoutParams
    }

    override fun getOwner(): ViewGroup {
        return this
    }

    override fun onActive() {
        val pageListPlay = PageListPlayManager.get(mCategory)
        val exoPlayer = pageListPlay.exoPlayer
        val playerView = pageListPlay.playerView
        val controllerView = pageListPlay.controllerView
        if (playerView == null || exoPlayer == null) return

        pageListPlay.switchPlayerView(playerView, true)

        val parent: ViewParent? = playerView.parent
        if (parent != this) {
            if (parent != null) {
                (parent as ViewGroup).removeView(playerView)
                (parent as ListPlayerView).inActive()
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
        val pageListPlay = PageListPlayManager.get(mCategory)
        val exoPlayer = pageListPlay.exoPlayer
        val controllerView = pageListPlay.controllerView
        if (controllerView == null || exoPlayer == null) return
        exoPlayer.repeatMode = Player.REPEAT_MODE_OFF  // 一定要关闭，否则导致 OOM
        exoPlayer.playWhenReady = false
        exoPlayer.removeListener(this)   // 不移除的话，当一个item player状态改变时，所有item的状态都会改变
        controllerView.removeVisibilityListener(this) // 不移除的话，当controller出现时，所有item的play都会出现
        coverView.visibility = VISIBLE
        playBtn.visibility = VISIBLE
        playBtn.setImageResource(R.drawable.icon_video_play)
    }

    override fun isPlaying(): Boolean {
        return isPlaying
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val pageListPlay = PageListPlayManager.get(mCategory)
        pageListPlay.controllerView?.show()
        return true
    }

    override fun onVisibilityChange(visibility: Int) {
        playBtn.visibility = visibility
        playBtn.setImageResource(if (isPlaying()) R.drawable.icon_video_pause else R.drawable.icon_video_play)
    }


    private var playbackState = Player.STATE_IDLE
    override fun onPlaybackStateChanged(playbackState: Int) {
        val pageListPlay = PageListPlayManager.get(mCategory)
        val exoPlayer = pageListPlay.exoPlayer ?: return
        this.playbackState = playbackState
        isPlaying =
            playbackState == Player.STATE_READY && exoPlayer.bufferedPosition != 0L
        switchState()
    }

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
        val pageListPlay = PageListPlayManager.get(mCategory)
        val exoPlayer = pageListPlay.exoPlayer ?: return
        isPlaying =
            playbackState == Player.STATE_READY && exoPlayer.bufferedPosition != 0L && playWhenReady
        switchState()
    }

    private fun switchState() {
        if (isPlaying) {
            coverView.visibility = GONE
            bufferView.visibility = GONE
        } else {
            bufferView.visibility = VISIBLE
        }
        playBtn.setImageResource(if (isPlaying) R.drawable.icon_video_pause else R.drawable.icon_video_play)
    }

    fun getPlayerControllerView(): PlayerControlView? {
        val pageListPlay = PageListPlayManager.get(mCategory)
        return pageListPlay.controllerView
    }
}