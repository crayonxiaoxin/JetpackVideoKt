package com.github.crayonxiaoxin.ppjoke_kt.ui.my

import android.content.Context
import android.view.View
import android.widget.TextView
import com.github.crayonxiaoxin.ppjoke_kt.R
import com.github.crayonxiaoxin.ppjoke_kt.ui.home.FeedAdapter
import com.github.crayonxiaoxin.ppjoke_kt.utils.TimeUtils
import com.github.crayonxiaoxin.ppjoke_kt.utils.UserManager

open class ProfileListAdapter : FeedAdapter {
    constructor(context: Context, mCategory: String) : super(context, mCategory)

    override fun getItemViewType(position: Int): Int {
        if (isCommentTab(position)) {
            return R.layout.layout_feed_type_comment
        }
        return super.getItemViewType(position)
    }

    private fun isCommentTab(position: Int): Boolean {
        if (mCategory == ProfileActivity.TAB_TYPE_COMMENT) {
            return true
        } else if (mCategory == ProfileActivity.TAB_TYPE_ALL) {
            val item = getItem(position)
            if (item?.topComment != null && (item.topComment?.userId
                    ?: "") == UserManager.userId()
            ) {
                return true
            }
        }
        return false
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        getItem(position)?.let { item ->
            val dissView: View = holder.itemView.findViewById(R.id.diss)
            val deleteView: View = holder.itemView.findViewById(R.id.delete)
            val createTimeView: TextView = holder.itemView.findViewById(R.id.create_time)
            createTimeView.visibility = View.VISIBLE
            createTimeView.text = TimeUtils.calculate(item.createTime)

            val isCommentTab = isCommentTab(position)
            if (isCommentTab){
                dissView.visibility = View.GONE
            }

            deleteView.setOnClickListener {
                if (isCommentTab) {

                }else{

                }
            }
        }
    }
}