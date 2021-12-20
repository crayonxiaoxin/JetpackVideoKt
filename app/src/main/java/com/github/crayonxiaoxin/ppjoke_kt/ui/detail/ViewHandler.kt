package com.github.crayonxiaoxin.ppjoke_kt.ui.detail

import android.view.View
import androidx.activity.viewModels
import androidx.annotation.CallSuper
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.crayonxiaoxin.lib_common.view.EmptyView
import com.github.crayonxiaoxin.ppjoke_kt.R
import com.github.crayonxiaoxin.ppjoke_kt.databinding.LayoutFeedDetailBottomInateractionBinding
import com.github.crayonxiaoxin.ppjoke_kt.model.Feed
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

open class ViewHandler(val mActivity: FragmentActivity) {

    val viewModel: FeedDetailViewModel by mActivity.viewModels()
    lateinit var mRecyclerView: RecyclerView
    lateinit var adapter: FeedDetailAdapter
    var emptyView: EmptyView? = null
    lateinit var mInateractionBinding: LayoutFeedDetailBottomInateractionBinding
    var mFeed: Feed? = null

    @CallSuper
    open fun bindInitData(feed: Feed) {
        this.mFeed = feed
        mRecyclerView.layoutManager = LinearLayoutManager(mActivity)
        mRecyclerView.itemAnimator = null
        adapter = FeedDetailAdapter()
        setAdapter()

        viewModel.itemId = feed.itemId ?: 0
        mActivity.lifecycleScope.launch {
            viewModel.getList().collect {
                adapter.submit(it)
            }
        }
        adapter.addLoadStateListener {
            when (it.refresh) {
                is LoadState.NotLoading, is LoadState.Error -> {
                    if (adapter.itemCount == 0) {
                        emptyView?.visibility = View.VISIBLE
                        emptyView?.setTitle(mActivity.getString(R.string.feed_comment_empty))
                    } else {
                        emptyView?.visibility = View.GONE
                    }
                }
                else -> {
                }
            }
        }
        mInateractionBinding.inputView.setOnClickListener {

        }

    }

    open fun setAdapter() {
        mRecyclerView.adapter = adapter
    }
}