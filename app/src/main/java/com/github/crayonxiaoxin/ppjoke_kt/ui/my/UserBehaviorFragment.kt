package com.github.crayonxiaoxin.ppjoke_kt.ui.my

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.crayonxiaoxin.lib_common.extension.FlowBus
import com.github.crayonxiaoxin.lib_nav_annotation.FragmentDestination
import com.github.crayonxiaoxin.ppjoke_kt.model.Feed
import com.github.crayonxiaoxin.ppjoke_kt.base.AbsListFragment
import com.github.crayonxiaoxin.ppjoke_kt.exoplayer.PageListPlayDetector
import com.github.crayonxiaoxin.ppjoke_kt.exoplayer.PageListPlayManager
import com.github.crayonxiaoxin.ppjoke_kt.ui.InteractionPresenter
import com.github.crayonxiaoxin.ppjoke_kt.ui.home.FeedAdapter
import kotlinx.coroutines.launch

class UserBehaviorFragment : AbsListFragment<Feed, UserBehaviorViewModel, FeedAdapter>() {
    private var playDetector: PageListPlayDetector? = null
    private var shouldPause: Boolean = true

    override val viewModel: UserBehaviorViewModel by viewModels()

    companion object {
        val KEY_BEHAVIOR_TYPE = "key_behavior_type"
        fun newInstance(behavior: Int): UserBehaviorFragment {
            val args = Bundle()
            args.putInt(KEY_BEHAVIOR_TYPE, behavior)
            val fragment = UserBehaviorFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun initAdapter(): FeedAdapter {
        viewModel.behavior = arguments?.getInt(KEY_BEHAVIOR_TYPE) ?: 0
        return object : FeedAdapter(requireContext(), "behavior${viewModel.behavior}") {
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
                FlowBus.observe<Feed>(InteractionPresenter.DATA_FROM_INTERACTION) {
                    adapter.update(it)
                }
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
        PageListPlayManager.release("behavior${viewModel.behavior}")
        super.onDestroy()
    }

}