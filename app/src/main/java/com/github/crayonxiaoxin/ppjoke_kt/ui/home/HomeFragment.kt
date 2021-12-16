package com.github.crayonxiaoxin.ppjoke_kt.ui.home

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.crayonxiaoxin.lib_nav_annotation.FragmentDestination
import com.github.crayonxiaoxin.ppjoke_kt.model.Feed
import com.github.crayonxiaoxin.ppjoke_kt.base.AbsListFragment
import com.github.crayonxiaoxin.ppjoke_kt.exoplayer.PageListPlayDetector
import kotlinx.coroutines.launch

@FragmentDestination("main/tabs/home", asStarter = true)
class HomeFragment() : AbsListFragment<Feed, HomeViewModel, FeedAdapter>() {
    private var playDetector: PageListPlayDetector? = null
    private val KEY_FEED_TYPE = "key_fed_type"
    private var shouldPause: Boolean = true

    override val viewModel: HomeViewModel by viewModels()

    fun newInstance(feedType: String): HomeFragment {
        val args = Bundle()
        args.putString(KEY_FEED_TYPE, feedType)
        val fragment = HomeFragment()
        fragment.arguments = args
        return fragment
    }

    override fun initAdapter(): FeedAdapter {
        viewModel.feedType = arguments?.getString(KEY_FEED_TYPE) ?: "all"
        return object : FeedAdapter(requireContext(), viewModel.feedType) {
            override fun onViewAttachedToWindow(holder: ViewHolder) {
                super.onViewAttachedToWindow(holder)
                if (holder.isVideoItem) {
                    playDetector?.addTarget(holder.listPlayerView)
                }
            }

            override fun onViewDetachedFromWindow(holder: ViewHolder) {
                super.onViewDetachedFromWindow(holder)
                if (holder.isVideoItem) {
                    playDetector?.removeTarget(holder.listPlayerView)
                }
            }
        }
    }

    override fun afterCreateView() {
        playDetector = PageListPlayDetector(this, recyclerView)
        adapter.setOnItemClickListener {
            // 如果是视频，不需要暂停
            shouldPause = it.itemType != Feed.TYPE_VIDEO
            lifecycleScope.launch {
//                adapter.update(it.copy(feeds_text = it.feeds_text + " haha"))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        shouldPause = true
        // 防止从后台切换时，多个 fragment 同时播放
        if (parentFragment != null) {
            if (requireParentFragment().isVisible && isVisible) {
                playDetector?.onResume()
            }
        } else {
            if (isVisible) {
                playDetector?.onResume()
            }
        }
    }

    override fun onPause() {
        if (shouldPause) {
            playDetector?.onPause()
        }
        super.onPause()
    }

    // 从其他页面切回来时
    override fun onHiddenChanged(hidden: Boolean) {
        if (hidden) {
            playDetector?.onPause()
        } else {
            playDetector?.onResume()
        }
    }

    override fun onDestroy() {
        playDetector = null
        super.onDestroy()
    }

}