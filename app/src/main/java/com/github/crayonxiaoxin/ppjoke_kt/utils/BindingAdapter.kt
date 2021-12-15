package com.github.crayonxiaoxin.ppjoke_kt.utils

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.github.crayonxiaoxin.lib_common.utils.dp
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

@SuppressLint("CheckResult")
@BindingAdapter("image_url", "is_circle", "radius", requireAll = false)
fun setImageUrl(view: ImageView, url: String, isCircle: Boolean = false, radius: Int = 0) {
    val builder = Glide.with(view).load(url)
    if (isCircle) {
        builder.transform(CircleCrop())
    } else if (radius > 0) {
        builder.transform(RoundedCornersTransformation(radius.dp, 0))
    }
    val layoutParams = view.layoutParams
    if (layoutParams != null && layoutParams.width > 0 && layoutParams.height > 0) {
        builder.override(layoutParams.width, layoutParams.height)
    }
    builder.into(view)
}

@BindingAdapter("blur_url", "radius", requireAll = false)
fun setBlurImageUrl(view: ImageView, url: String, radius: Int = 0) {
    Glide.with(view).load(url).override(50).transform(BlurTransformation(radius)).dontAnimate()
        .into(object : CustomTarget<Drawable>() {

            override fun onLoadCleared(placeholder: Drawable?) {
            }

            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                view.background = resource
            }
        })
}


@SuppressLint("CheckResult")
fun ImageView.setImageUri(uri: Uri) {
    val builder = Glide.with(this).load(uri)
    val layoutParams = layoutParams
    if (layoutParams != null && layoutParams.width > 0 && layoutParams.height > 0) {
        builder.override(layoutParams.width, layoutParams.height)
    }
    builder.into(this)
}