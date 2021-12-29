package com.github.crayonxiaoxin.ppjoke_kt.ui.my

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.crayonxiaoxin.lib_common.extension.FlowBus
import com.github.crayonxiaoxin.ppjoke_kt.base.AbsListFragment
import com.github.crayonxiaoxin.ppjoke_kt.exoplayer.PageListPlayDetector
import com.github.crayonxiaoxin.ppjoke_kt.exoplayer.PageListPlayManager
import com.github.crayonxiaoxin.ppjoke_kt.model.Feed
import com.github.crayonxiaoxin.ppjoke_kt.ui.InteractionPresenter
import kotlinx.coroutines.launch

class ProfileListFragment : AbsListFragment<Feed, ProfileViewModel, ProfileListAdapter>() {
    companion object {
        fun newInstance(category: String): ProfileListFragment {
            val args = Bundle()
            args.putString(ProfileActivity.KEY_TAB_TYPE, category)
            val fragment = ProfileListFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override val viewModel: ProfileViewModel by viewModels()
    private var playDetector: PageListPlayDetector? = null
    private var shouldPause: Boolean = true

    override fun initAdapter(): ProfileListAdapter {
        viewModel.profileType = arguments?.getString(ProfileActivity.KEY_TAB_TYPE) ?: ""

        return object : ProfileListAdapter(requireContext(), viewModel.profileType) {
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
        if (viewModel.profileType == ProfileActivity.TAB_TYPE_COMMENT) {
            playDetector?.onPause()
        } else {
            playDetector?.onResume()
        }
    }

    override fun onPause() {
        super.onPause()
        if (shouldPause) {
            playDetector?.onPause()
        }
    }

    // 从其他页面切回来时
    override fun onHiddenChanged(hidden: Boolean) {
        if (hidden) {
            playDetector?.onPause()
        } else {
            playDetector?.onResume()
        }
    }

    override fun onDestroyView() {
        Log.e("TAG", "onDestroyView: ${viewModel.profileType}")
        PageListPlayManager.release(viewModel.profileType)
        super.onDestroyView()
    }
}