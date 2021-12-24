package com.github.crayonxiaoxin.ppjoke_kt.ui.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.crayonxiaoxin.ppjoke_kt.model.Feed

class FeedDetailActivity : AppCompatActivity() {

    private var viewHandler: ViewHandler? = null

    companion object {
        const val KEY_FEED = "key_feed"
        const val KEY_FEED_CATEGORY = "key_feed_category"
        fun startActivity(context: Context, feed: Feed, category: String) {
            context.startActivity(Intent(context, FeedDetailActivity::class.java).apply {
                putExtra(KEY_FEED, feed)
                putExtra(KEY_FEED_CATEGORY, category)
            })
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val feed = intent.getSerializableExtra(KEY_FEED) as Feed?
        if (feed == null) {
            finish()
        } else {
            if (feed.itemType == Feed.TYPE_IMAGE) {
                viewHandler = ImageViewHandler(this)
            } else {
                viewHandler = VideoViewHandler(this)
            }
            viewHandler?.bindInitData(feed)
        }
    }

    override fun onResume() {
        super.onResume()
        viewHandler?.onResume()
    }

    override fun onPause() {
        super.onPause()
        viewHandler?.onPause()
    }

    override fun onBackPressed() {
        viewHandler?.onBackPressed()
        super.onBackPressed()
    }

}