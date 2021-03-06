package com.github.crayonxiaoxin.ppjoke_kt.ui.detail

import android.net.Uri
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.CallSuper
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.crayonxiaoxin.lib_common.view.EmptyView
import com.github.crayonxiaoxin.ppjoke_kt.R
import com.github.crayonxiaoxin.ppjoke_kt.databinding.LayoutFeedDetailBottomInateractionBinding
import com.github.crayonxiaoxin.ppjoke_kt.model.Comment
import com.github.crayonxiaoxin.ppjoke_kt.model.Feed
import com.github.crayonxiaoxin.ppjoke_kt.ui.InteractionPresenter
import com.github.crayonxiaoxin.ppjoke_kt.ui.publish.PreviewActivity
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

open class ViewHandler(val mActivity: FragmentActivity) {

    val viewModel: FeedDetailViewModel by mActivity.viewModels()
    lateinit var mRecyclerView: RecyclerView
    lateinit var adapter: FeedDetailAdapter
    var emptyView: EmptyView? = null
    lateinit var mInateractionBinding: LayoutFeedDetailBottomInateractionBinding
    var mFeed: Feed? = null
    var commentDialog: CommentDialog? = null

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
                    toggleEmptyView()
                }
                else -> {
                }
            }
        }
        adapter.setOnItemDeleteListener { comment ->
            AlertDialog.Builder(mActivity)
                .setMessage("?????????????????????????????????")
                .setPositiveButton("??????") { dialog, _ ->
                    dialog.dismiss()
                    viewModel.deleteComment(comment) {
                        if (it) {
                            adapter.delete(comment)
                            feed.ugc?.commentCount?.let {
                                feed.ugc?.commentCount = it - 1
                                feed.ugc?.notifyChange()
                            }
                            InteractionPresenter.notify(feed)
                        }
                    }
                }
                .setNegativeButton("??????") { dialog, _ ->
                    dialog.dismiss()
                }
                .create().show()
        }
        adapter.setOnPreviewListener {
            val isVideo = it.commentType == Comment.COMMENT_TYPE_VIDEO
            val url = if (isVideo) it.videoUrl else it.imageUrl
            url?.let {
                mActivity.startActivity(
                    PreviewActivity.intentStartActivity(
                        mActivity,
                        Uri.parse(url),
                        isVideo
                    )
                )
            }
        }
        adapter.addOnPagesUpdatedListener {
            toggleEmptyView()
        }
        mInateractionBinding.inputView.setOnClickListener {
            if (commentDialog == null) {
                commentDialog = CommentDialog.newInstance(feed.itemId ?: 0)
            }
            commentDialog?.setOnCommentAddedListener {
                feed.ugc?.commentCount?.let {
                    feed.ugc?.commentCount = it + 1
                    feed.ugc?.notifyChange()
                }
                mActivity.lifecycleScope.launch {
                    InteractionPresenter.notify(feed)
                    // add ?????????????????????????????????????????????????????????glide????????????
                    adapter.add(it, false)
                }
            }
            commentDialog?.isCancelable = true
            commentDialog?.show(mActivity.supportFragmentManager, "comment-dialog")
        }

    }

    private fun toggleEmptyView() {
        if (adapter.itemCount == 0) {
            emptyView?.visibility = View.VISIBLE
            emptyView?.setTitle(mActivity.getString(R.string.feed_comment_empty))
        } else {
            emptyView?.visibility = View.GONE
        }
    }

    // ???????????????????????????  ???????????? ConcatAdapter ?????? header & footer
    open fun setAdapter() {
        mRecyclerView.adapter = adapter
    }

    open fun onPause(){
    }

    open fun onResume(){
    }

    open fun onBackPressed(){

    }
}