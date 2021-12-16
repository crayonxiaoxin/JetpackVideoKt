package com.github.crayonxiaoxin.ppjoke_kt.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.crayonxiaoxin.ppjoke_kt.BR
import com.github.crayonxiaoxin.ppjoke_kt.R
import com.github.crayonxiaoxin.ppjoke_kt.base.AbsPagingAdapter
import com.github.crayonxiaoxin.ppjoke_kt.databinding.LayoutFeedTypeImageBinding
import com.github.crayonxiaoxin.ppjoke_kt.databinding.LayoutFeedTypeVideoBinding
import com.github.crayonxiaoxin.ppjoke_kt.model.Feed
import com.github.crayonxiaoxin.ppjoke_kt.ui.view.ListPlayerView

open class FeedAdapter(val context: Context, val mCategory: String = "") :
    AbsPagingAdapter<Feed, FeedAdapter.ViewHolder>(diff) {

    private var mListener: ((Feed) -> Unit)? = null

    override fun getItemViewType(position: Int): Int {
        getItem(position)?.let {
            if (it.itemType == Feed.TYPE_IMAGE) {
                return R.layout.layout_feed_type_image
            } else if (it.itemType == Feed.TYPE_VIDEO) {
                return R.layout.layout_feed_type_video
            }
        }
        return 0
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ViewDataBinding =
            DataBindingUtil.inflate(LayoutInflater.from(context), viewType, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position) ?: return
        holder.bindData(item)
        holder.itemView.setOnClickListener { mListener?.invoke(item) }
    }

    fun setOnItemClickListener(listener: ((Feed) -> Unit)?) {
        this.mListener = listener
    }

    inner class ViewHolder(val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        var listPlayerView: ListPlayerView? = null
        val isVideoItem: Boolean get() = binding is LayoutFeedTypeVideoBinding

        fun bindData(feed: Feed) {
            binding.setVariable(BR.feed, feed)
            binding.setVariable(BR.lifeCycleOwner, context)
            if (binding is LayoutFeedTypeImageBinding) {
                binding.feedImage.bindData(feed.width ?: 0, feed.height ?: 0, 16, feed.cover ?: "")
            } else if (binding is LayoutFeedTypeVideoBinding) {
                binding.listPlayerView.bindData(
                    mCategory,
                    feed.width ?: 0,
                    feed.height ?: 0,
                    feed.cover ?: "",
                    feed.url ?: ""
                )
                listPlayerView = binding.listPlayerView
            }
        }
    }

    companion object {
        val diff = object :
            DiffUtil.ItemCallback<Feed>() {
            override fun areItemsTheSame(oldItem: Feed, newItem: Feed): Boolean {
                return oldItem.itemId == newItem.itemId
            }

            override fun areContentsTheSame(oldItem: Feed, newItem: Feed): Boolean {
                return oldItem == newItem
            }
        }
    }

}