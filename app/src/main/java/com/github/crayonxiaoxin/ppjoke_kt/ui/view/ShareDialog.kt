package com.github.crayonxiaoxin.ppjoke_kt.ui.view

import android.content.ComponentName
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.setMargins
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.crayonxiaoxin.lib_common.utils.dp
import com.github.crayonxiaoxin.lib_common.view.RoundFrameLayout
import com.github.crayonxiaoxin.lib_common.view.ViewHelper
import com.github.crayonxiaoxin.ppjoke_kt.R


class ShareDialog : AlertDialog {

    private lateinit var shareAdapter: ShareAdapter
    private var shareContent: String = ""
    private var mShareItems: MutableList<ResolveInfo> = ArrayList()
    private var mListener: (() -> Unit)? = null

    constructor(context: Context) : super(context)
    constructor(context: Context, themeResId: Int) : super(context, themeResId)
    constructor(
        context: Context,
        cancelable: Boolean,
        cancelListener: DialogInterface.OnCancelListener?
    ) : super(context, cancelable, cancelListener)

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)

        val roundFrameLayout = RoundFrameLayout(context)
        roundFrameLayout.setBackgroundColor(Color.WHITE)
        roundFrameLayout.setViewOutline(20.dp, ViewHelper.RADIUS_TOP)

        val recyclerView = RecyclerView(context)
        recyclerView.layoutManager = GridLayoutManager(context, 4)
        val layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(20.dp)
        recyclerView.layoutParams = layoutParams
        shareAdapter = ShareAdapter()
        recyclerView.adapter = shareAdapter

        roundFrameLayout.addView(recyclerView)
        setContentView(roundFrameLayout)

        window?.let {
            it.setGravity(Gravity.BOTTOM)
            it.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            it.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        queryShareItems()
    }

    private fun queryShareItems() {
        val intent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
        }
        val allow = arrayOf("com.tencent.mobileqq", "com.tencent.mm", "com.tencent.tim")
        val queryIntentActivities = context.packageManager.queryIntentActivities(intent, 0)
        queryIntentActivities.forEach {
            if (allow.contains(it.activityInfo.packageName)) {
                mShareItems.add(it)
            }
        }
        shareAdapter.notifyDataSetChanged()
    }

    fun setShareContent(shareContent: String) {
        this.shareContent = shareContent
    }

    fun setShareItemClickListener(listener: () -> Unit) {
        mListener = listener
    }

    inner class ShareAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val inflate = layoutInflater.inflate(R.layout.layout_share_item, parent, false)
            return object : RecyclerView.ViewHolder(inflate) {}
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val resolveInfo = mShareItems[position]
            val imageView: PPImageView = holder.itemView.findViewById(R.id.share_icon)
            val loadIcon = resolveInfo.loadIcon(context.packageManager)
            imageView.setImageDrawable(loadIcon)
            val textView: TextView = holder.itemView.findViewById(R.id.share_text)
            val loadLabel = resolveInfo.loadLabel(context.packageManager)
            textView.text = loadLabel
            holder.itemView.setOnClickListener {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "text/plain"
                    component = ComponentName(
                        resolveInfo.activityInfo.packageName,
                        resolveInfo.activityInfo.name
                    )
                    putExtra(Intent.EXTRA_TEXT, shareContent)
                }
                context.startActivity(intent)
                mListener?.invoke()
            }
        }

        override fun getItemCount(): Int {
            return mShareItems.size
        }

    }
}
