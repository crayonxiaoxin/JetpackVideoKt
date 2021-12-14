package com.github.crayonxiaoxin.ppjoke_kt.ui.home

import android.graphics.Color
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

    private var mListener: ((Feed) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val textView = TextView(parent.context)
        textView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        textView.setTextColor(Color.RED)
        textView.textSize = 30f
        return ViewHolder(textView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position) ?: return
        val textView = (holder.itemView as TextView)
        textView.text = getItem(position)?.feeds_text ?: ""
        textView.setOnClickListener {
            mListener?.invoke(item)
        }
    }

    fun setOnItemClickListener(listener: ((Feed) -> Unit)?) {
        this.mListener = listener
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }


}