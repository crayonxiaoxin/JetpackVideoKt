package com.github.crayonxiaoxin.ppjoke_kt.ui.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import com.github.crayonxiaoxin.lib_common.utils.StatusBar
import com.github.crayonxiaoxin.lib_common.utils.dp
import com.github.crayonxiaoxin.ppjoke_kt.R
import com.github.crayonxiaoxin.ppjoke_kt.base.AbsHeaderFooter
import com.github.crayonxiaoxin.ppjoke_kt.databinding.ActivityFeedDetailTypeVideoBinding
import com.github.crayonxiaoxin.ppjoke_kt.databinding.LayoutFeedDetailTypeVideoHeaderBinding
import com.github.crayonxiaoxin.ppjoke_kt.model.Feed
import com.github.crayonxiaoxin.ppjoke_kt.ui.view.FullScreenPlayerView

class VideoViewHandler(mActivity: FragmentActivity) : ViewHandler(mActivity) {
    private var category: String = ""
    private var backPressed: Boolean = false
    private var playerView: FullScreenPlayerView
    private var coordinator: CoordinatorLayout
    var binding: ActivityFeedDetailTypeVideoBinding =
        DataBindingUtil.setContentView(mActivity, R.layout.activity_feed_detail_type_video)

    init {
        StatusBar.fitSystemBar(mActivity, false, darkIcons = false, fullscreen = true)
        mRecyclerView = binding.recyclerView
        mInateractionBinding = binding.bottomInteraction
        binding.actionClose.setOnClickListener { mActivity.finish() }

        playerView = binding.playerView
        coordinator = binding.coordinator

        val authorInfoRoot = binding.authorInfo.root
        val params = authorInfoRoot.layoutParams as CoordinatorLayout.LayoutParams
        params.behavior = ViewAnchorBehavior(R.id.player_view)
        authorInfoRoot.layoutParams = params

        val layoutParams = playerView.layoutParams as CoordinatorLayout.LayoutParams
        val behavior = layoutParams.behavior as ViewZoomBehavior
        behavior.setOnViewZoomCallback { height ->
            val bottom = playerView.bottom
            val moveUp = height < bottom
            val inputHeight = mInateractionBinding.root.measuredHeight
            val fullscreen =
                if (!moveUp) height >= coordinator.bottom - inputHeight - 2 else height >= coordinator.bottom - 2
            setViewAppearance(fullscreen)
        }
    }

    private fun setViewAppearance(fullscreen: Boolean) {
        binding.fullscreen = fullscreen
        mInateractionBinding.fullscreen = fullscreen
        val fullscreenAuthorRoot = binding.fullscreenAuthorInfo.root
        val layoutParams = fullscreenAuthorRoot.layoutParams as CoordinatorLayout.LayoutParams
        layoutParams.topMargin = 30.dp
        fullscreenAuthorRoot.layoutParams = layoutParams
        fullscreenAuthorRoot.visibility = if (fullscreen) View.VISIBLE else View.GONE
        val inputHeight = mInateractionBinding.root.measuredHeight
        binding.playerView.getPlayerControllerView()?.let { controllerView ->
            val ctrlHeight = controllerView.measuredHeight
            val ctrlBottom = controllerView.bottom
            controllerView.y =
                if (fullscreen) (ctrlBottom - ctrlHeight - inputHeight).toFloat() else (ctrlBottom - ctrlHeight).toFloat()
        }
    }

    override fun onBackPressed() {
        backPressed = true
    }

    override fun onPause() {
        if (!backPressed) {
            playerView.inActive()
        }
    }

    override fun onResume() {
        backPressed = false
        playerView.post {
            playerView.onActive()
        }
    }

    override fun bindInitData(feed: Feed) {
        super.bindInitData(feed)
        binding.feed = feed
        binding.owner = mActivity

        category = mActivity.intent.getStringExtra(FeedDetailActivity.KEY_FEED_CATEGORY) ?: ""
        binding.playerView.bindData(
            category,
            feed.width ?: 0,
            feed.height ?: 0,
            feed.cover ?: "",
            feed.url ?: ""
        )
        binding.playerView.post {
            val fullscreen =
                binding.playerView.bottom >= binding.coordinator.bottom - mInateractionBinding.root.measuredHeight - 2
            setViewAppearance(fullscreen)
        }
    }

    override fun setAdapter() {
        val header = object : AbsHeaderFooter() {
            override fun setView(parent: ViewGroup): View {
                val headerBinding = LayoutFeedDetailTypeVideoHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                headerBinding.feed = mFeed
                emptyView = headerBinding.emptyView
                return headerBinding.root
            }
        }
        mRecyclerView.adapter = adapter.withLoadStateHeader(header)
    }
}