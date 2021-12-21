package com.github.crayonxiaoxin.ppjoke_kt.ui.detail

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.github.crayonxiaoxin.lib_common.utils.StatusBar
import com.github.crayonxiaoxin.ppjoke_kt.R
import com.github.crayonxiaoxin.ppjoke_kt.base.AbsHeaderFooter
import com.github.crayonxiaoxin.ppjoke_kt.databinding.ActivityFeedDetailTypeImageBinding
import com.github.crayonxiaoxin.ppjoke_kt.databinding.LayoutFeedDetailTypeImageHeaderBinding
import com.github.crayonxiaoxin.ppjoke_kt.model.Feed

class ImageViewHandler(mActivity: FragmentActivity) : ViewHandler(mActivity) {
    private lateinit var mHeaderBinding: LayoutFeedDetailTypeImageHeaderBinding
    val binding: ActivityFeedDetailTypeImageBinding =
        DataBindingUtil.setContentView(mActivity, R.layout.activity_feed_detail_type_image)

    init {
        StatusBar.fitSystemBar(mActivity, true, true)
        mRecyclerView = binding.recyclerView
        mInateractionBinding = binding.interactionLayout
        binding.actionBack.setOnClickListener { mActivity.finish() }
    }

    override fun setAdapter() {
        val header = object : AbsHeaderFooter() {
            override fun setView(parent: ViewGroup): View {
                mHeaderBinding = LayoutFeedDetailTypeImageHeaderBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                mHeaderBinding.feed = mFeed
                val width = mFeed?.width ?: 0
                val height = mFeed?.height ?: 0
                mHeaderBinding.headerImage.bindData(
                    width,
                    height,
                    if (width > height) 0 else 16,
                    mFeed?.cover ?: ""
                )
                emptyView = mHeaderBinding.emptyView
                return mHeaderBinding.root
            }
        }
        mRecyclerView.adapter = adapter.withLoadStateHeader(header)
    }

    override fun bindInitData(feed: Feed) {
        super.bindInitData(feed)
        binding.feed = feed
        binding.owner = mActivity
        binding.authorInfoLayout.feed = feed
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // 控制 topBar 的显示隐藏
                val visible =
                    mHeaderBinding.root.top <= -mHeaderBinding.headerAuthorInfo.root.measuredHeight
                binding.authorInfoLayout.root.visibility = if (visible) View.VISIBLE else View.GONE
                binding.title.visibility = if (visible) View.GONE else View.VISIBLE
            }
        })
    }


}