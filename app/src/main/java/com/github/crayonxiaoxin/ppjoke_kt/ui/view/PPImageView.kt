package com.github.crayonxiaoxin.ppjoke_kt.ui.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.github.crayonxiaoxin.lib_common.utils.PixUtils
import com.github.crayonxiaoxin.lib_common.utils.dp
import com.github.crayonxiaoxin.lib_common.view.ViewHelper
import com.github.crayonxiaoxin.ppjoke_kt.utils.setImageUrl

class PPImageView : AppCompatImageView {
    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        ViewHelper.setViewOutline(this, attrs, defStyleAttr, 0)
    }


    fun bindData(
        widthPx: Int,
        heightPx: Int,
        marginLeft: Int,
        imageUrl: String,
        maxWidthPx: Int = PixUtils.getScreenWidth(),
        maxHeightPx: Int = PixUtils.getScreenHeight(),
    ) {
        if (widthPx <= 0 || heightPx <= 0) {
            Glide.with(this).load(imageUrl).into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    setSize(
                        resource.intrinsicWidth,
                        resource.intrinsicHeight,
                        marginLeft,
                        maxWidthPx,
                        maxHeightPx
                    )
                    setImageDrawable(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
            return
        }
        setSize(widthPx, heightPx, marginLeft, maxWidthPx, maxHeightPx)
        setImageUrl(this, imageUrl)
    }

    private fun setSize(
        width: Int,
        height: Int,
        marginLeft: Int,
        maxWidthPx: Int,
        maxHeightPx: Int
    ) {
        var finalWidth = 0
        var finalHeight = 0
        var leftMargin = 0
        if (width > height) {
            finalWidth = maxWidthPx
            finalHeight = (height / (width * 1.0f / finalHeight)).toInt()
            leftMargin = 0
        } else {
            finalHeight = maxHeightPx
            finalWidth = (width / (height * 1.0f / finalHeight)).toInt()
            leftMargin = marginLeft.dp
        }
        val params = layoutParams
        params.width = finalWidth
        params.height = finalHeight
        if (params is FrameLayout.LayoutParams) {
            params.leftMargin = leftMargin
        } else if (params is LinearLayout.LayoutParams) {
            params.leftMargin = leftMargin
        }
        layoutParams = params
    }
}