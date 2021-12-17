package com.github.crayonxiaoxin.ppjoke_kt.ui.find

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.crayonxiaoxin.ppjoke_kt.base.AbsPagingAdapter
import com.github.crayonxiaoxin.ppjoke_kt.databinding.LayoutTagListItemBinding
import com.github.crayonxiaoxin.ppjoke_kt.model.TagList

class TagListAdapter : AbsPagingAdapter<TagList, TagListAdapter.ViewHolder>(diff) {

    private var followClickListener: ((item: TagList) -> Unit)? = null
    fun setOnFollowClickListener(listener: (item: TagList) -> Unit) {
        this.followClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            LayoutTagListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position) ?: return
        holder.bindData(item)
        holder.itemView.setOnClickListener { itemClickListener?.invoke(item) }
        holder.binding.actionFollow.setOnClickListener {
            followClickListener?.invoke(item)
        }
    }

    inner class ViewHolder(val binding: LayoutTagListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindData(item: TagList?) {
            binding.tagList = item
        }
    }

    companion object {
        val diff = object : DiffUtil.ItemCallback<TagList>() {
            override fun areItemsTheSame(oldItem: TagList, newItem: TagList): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: TagList, newItem: TagList): Boolean {
                return oldItem == newItem
            }

        }
    }


}