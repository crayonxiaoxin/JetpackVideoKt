package com.github.crayonxiaoxin.ppjoke_kt.ui.publish

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.github.crayonxiaoxin.lib_common.utils.StatusBar
import com.github.crayonxiaoxin.ppjoke_kt.R
import com.github.crayonxiaoxin.ppjoke_kt.databinding.ActivityPreviewBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.FileDataSource
import java.io.File

class PreviewActivity : AppCompatActivity(), View.OnClickListener {
    companion object {
        const val KEY_PREVIEW_URI = "preview_uri"
        const val KEY_PREVIEW_VIDEO = "preview_video"
        const val KEY_PREVIEW_BTN_TEXT = "preview_btn_text"
        fun intentStartActivity(
            context: Context,
            uri: Uri,
            isVideo: Boolean,
            btnText: String? = ""
        ): Intent {
            return Intent(context, PreviewActivity::class.java).apply {
                putExtra(KEY_PREVIEW_URI, uri)
                putExtra(KEY_PREVIEW_VIDEO, isVideo)
                putExtra(KEY_PREVIEW_BTN_TEXT, btnText)
            }
        }
    }

    private var player: ExoPlayer? = null
    private lateinit var binding: ActivityPreviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StatusBar.fitSystemBar(this, false, darkIcons = false)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_preview)
        val previewUri = intent.getParcelableExtra<Uri>(KEY_PREVIEW_URI)
        val btnText = intent.getStringExtra(KEY_PREVIEW_BTN_TEXT)
        val isVideo = intent.getBooleanExtra(KEY_PREVIEW_VIDEO, false)
        if (previewUri == null) {
            finish()
        } else {
            if (btnText.isNullOrEmpty()) {
                binding.actionOk.visibility = View.GONE
            } else {
                binding.actionOk.apply {
                    visibility = View.VISIBLE
                    text = btnText
                    setOnClickListener(this@PreviewActivity)
                }
            }
            binding.actionClose.setOnClickListener(this)
            if (isVideo) {
                previewVideo(previewUri)
            } else {
                previewImage(previewUri)
            }
        }
    }

    private fun previewImage(previewUri: Uri) {
        binding.photoView.visibility = View.VISIBLE
        Glide.with(this).load(previewUri).into(binding.photoView)
    }

    private fun previewVideo(previewUri: Uri) {
        binding.playerView.visibility = View.VISIBLE
        player = ExoPlayer.Builder(this).build()
        val file = File(previewUri.path ?: "")

        val uri = if (file.exists()) { // 本地文件
            val dataSpec = DataSpec.Builder().setUri(Uri.fromFile(file)).build()
            val dataSource = FileDataSource.Factory().createDataSource()
            try {
                dataSource.open(dataSpec)
                dataSpec.uri
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        } else { // 网络文件
            previewUri
        }
        if (uri == null) return
        val factory = ProgressiveMediaSource.Factory(DefaultDataSource.Factory(this))
        val mediaSource = factory.createMediaSource(MediaItem.fromUri(uri))
        player?.let {
            it.setMediaSource(mediaSource)
//            it.repeatMode = Player.REPEAT_MODE_ONE
            it.prepare()
            it.playWhenReady = true
            binding.playerView.player = it
        }
    }

    override fun onResume() {
        super.onResume()
        player?.playWhenReady = true
    }

    override fun onPause() {
        super.onPause()
        player?.playWhenReady = false
    }

    override fun onDestroy() {
        player?.let {
//            it.repeatMode = Player.REPEAT_MODE_OFF
            it.playWhenReady = false
            it.stop()
            it.release()
            player = null
        }
        super.onDestroy()
    }

    override fun onClick(v: View?) {
        val id = v?.id ?: return
        if (id == R.id.action_close) {
            finish()
        } else if (id == R.id.action_ok) {
            setResult(RESULT_OK, Intent())
            finish()
        }
    }
}