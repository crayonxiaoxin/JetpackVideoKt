package com.github.crayonxiaoxin.ppjoke_kt.ui.home

import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.crayonxiaoxin.ppjoke_kt.model.Feed

class FeedAdapter : PagingDataAdapter<Feed, FeedAdapter.ViewHolder>(object :
    DiffUtil.ItemCallback<Feed>() {
    override fun areItemsTheSame(oldItem: Feed, newItem: Feed): Boolean {
        return oldItem.itemId == newItem.itemId
    }

    override fun areContentsTheSame(oldItem: Feed, newItem: Feed): Boolean {
        return oldItem == newItem
    }
}) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val textView = TextView(parent.context)
        textView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        textView.setTextColor(Color.RED)
        textView.textSize = 30f
        Log.e("TAG", "onCreateViewHolder: " )
        return ViewHolder(textView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.e("TAG", "onBindViewHolder: ${getItem(position)?.feeds_text}")
        (holder.itemView as TextView).text = getItem(position)?.feeds_text ?: ""
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

}