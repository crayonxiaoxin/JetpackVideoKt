package com.github.crayonxiaoxin.ppjoke_kt.exoplayer

import android.view.LayoutInflater
import com.github.crayonxiaoxin.lib_common.global.AppGlobals
import com.github.crayonxiaoxin.ppjoke_kt.R
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.PlayerControlView
import com.google.android.exoplayer2.ui.PlayerView

class PageListPlay() {
    var exoPlayer: ExoPlayer? = null
    var playerView: PlayerView? = null
    var controllerView: PlayerControlView? = null
    var playUrl: String = ""

    init {
        val application = AppGlobals.application
        exoPlayer = ExoPlayer.Builder(application).build()
        val layoutInflater = LayoutInflater.from(application)
        playerView =
            layoutInflater.inflate(R.layout.layout_exo_player_view, null, false) as PlayerView?
        controllerView = layoutInflater.inflate(
            R.layout.layout_exo_player_controller_view,
            null,
            false
        ) as PlayerControlView?
        playerView?.player = exoPlayer
        controllerView?.player = exoPlayer
    }

    fun release() {
        exoPlayer?.let {
            it.playWhenReady = false
            it.stop()
            it.clearMediaItems()
            it.release()
            exoPlayer = null
        }
        playerView?.let {
            it.player = null
            playerView = null
        }
        controllerView?.let {
            it.player = null
            controllerView = null
        }
    }

    /**
     * 切换播放器view
     * @param attach true表示切换到新的view，false则恢复旧view
     */
    fun switchPlayerView(newPlayerView: PlayerView, attach: Boolean = false) {
        if (attach) {
            // 切断旧的关联
            this.playerView?.player = null
            // 关联新的 playerView
            newPlayerView.player = this.exoPlayer
        } else {
            // 恢复旧的关联
            this.playerView?.player = this.exoPlayer
            // 切断新的关联
            newPlayerView.player = null
        }
    }
}