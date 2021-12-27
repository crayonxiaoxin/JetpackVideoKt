package com.github.crayonxiaoxin.ppjoke_kt.ui.publish

import android.content.res.ColorStateList
import android.graphics.Typeface
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.github.crayonxiaoxin.lib_common.utils.dp
import com.github.crayonxiaoxin.ppjoke_kt.R
import com.github.crayonxiaoxin.ppjoke_kt.base.AbsPagingAdapter
import com.github.crayonxiaoxin.ppjoke_kt.model.TagList

class TagsAdapter : AbsPagingAdapter<TagList, RecyclerView.ViewHolder>(diff) {
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

    private var mListener: ((TagList) -> Unit)? = null
    fun setOnTagItemSelected(listener: (TagList) -> Unit) {
        this.mListener = listener
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val textView = holder.itemView as TextView
        val item = getItem(position) ?: return
        textView.text = item.title
        textView.setOnClickListener {
            mListener?.invoke(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val textView = TextView(parent.context).apply {
            textSize = 13f
            typeface = Typeface.DEFAULT_BOLD
            gravity = Gravity.CENTER_VERTICAL
            setTextColor(ContextCompat.getColor(parent.context, R.color.color_000))
            layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, 45.dp)
        }
        return object : RecyclerView.ViewHolder(textView) {}
    }
}