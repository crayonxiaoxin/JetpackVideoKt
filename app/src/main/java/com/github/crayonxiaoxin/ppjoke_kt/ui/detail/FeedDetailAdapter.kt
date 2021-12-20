package com.github.crayonxiaoxin.ppjoke_kt.ui.detail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.crayonxiaoxin.lib_common.utils.dp
import com.github.crayonxiaoxin.ppjoke_kt.base.AbsPagingAdapter
import com.github.crayonxiaoxin.ppjoke_kt.databinding.LayoutFeedCommentListItemBinding
import com.github.crayonxiaoxin.ppjoke_kt.model.Comment
import com.github.crayonxiaoxin.ppjoke_kt.utils.UserManager

class FeedDetailAdapter : AbsPagingAdapter<Comment, FeedDetailAdapter.ViewHolder>(diff) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position) ?: return
        holder.bindData(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LayoutFeedCommentListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    inner class ViewHolder(val binding: LayoutFeedCommentListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(comment: Comment) {
            binding.comment = comment
            val isAuthor = UserManager.userId() == comment.userId?.toString()
            if (isAuthor) {
                binding.labelAuthor.visibility = View.VISIBLE
                binding.commentDelete.visibility = View.VISIBLE
            } else {
                binding.labelAuthor.visibility = View.GONE
                binding.commentDelete.visibility = View.GONE
            }
            if (!comment.imageUrl.isNullOrEmpty()) {
                binding.commentCover.visibility = View.VISIBLE
                val dp200 = 200.dp
                binding.commentCover.bindData(
                    comment.width ?: 0,
                    comment.height ?: 0,
                    0,
                    comment.imageUrl ?: "",
                    dp200,
                    dp200
                )
                if (comment.videoUrl.isNullOrEmpty()) {
                    binding.videoIcon.visibility = View.GONE
                } else {
                    binding.videoIcon.visibility = View.VISIBLE
                }
            } else {
                binding.commentCover.visibility = View.GONE
                binding.videoIcon.visibility = View.GONE
            }

        }
    }

    companion object {
        val diff = object : DiffUtil.ItemCallback<Comment>() {
            override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
                return oldItem == newItem
            }

        }
    }
}