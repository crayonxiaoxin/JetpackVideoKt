package com.github.crayonxiaoxin.ppjoke_kt.ui

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.github.crayonxiaoxin.lib_common.extension.FlowBus
import com.github.crayonxiaoxin.lib_common.global.AppGlobals
import com.github.crayonxiaoxin.lib_common.global.toast
import com.github.crayonxiaoxin.ppjoke_kt.base.prepare
import com.github.crayonxiaoxin.ppjoke_kt.model.Comment
import com.github.crayonxiaoxin.ppjoke_kt.model.Feed
import com.github.crayonxiaoxin.ppjoke_kt.ui.view.ShareDialog
import com.github.crayonxiaoxin.ppjoke_kt.utils.UserManager
import com.github.crayonxiaoxin.ppjoke_kt.utils.apiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

object InteractionPresenter {
    const val DATA_FROM_INTERACTION = "data_from_interaction"

    @JvmStatic
    fun toggleFeedLiked(owner: LifecycleOwner, feed: Feed) {
        doAfterLogin {
            val res = prepare { apiService.toggleFeedLike(feed.itemId ?: 0) }
            if (res.isSuccess) {
                val hasLiked = res.getOrNull()?.hasLiked ?: false
                feed.ugc?.hasLiked = hasLiked
                if (hasLiked) {
                    feed.ugc?.hasdiss = false
                    feed.ugc?.likeCount?.let {
                        feed.ugc?.likeCount = it + 1
                    }
                }
                feed.ugc?.notifyChange()
                FlowBus.post(DATA_FROM_INTERACTION, feed)
            }
        }
    }

    @JvmStatic
    fun toggleFeedDiss(owner: LifecycleOwner, feed: Feed) {
        doAfterLogin {
            val res = prepare { apiService.dissFeed(feed.itemId ?: 0) }
            if (res.isSuccess) {
                val hasLiked = res.getOrNull()?.hasLiked ?: false
                feed.ugc?.hasdiss = hasLiked
                if (hasLiked) {
                    feed.ugc?.hasLiked = false
                    feed.ugc?.likeCount?.let {
                        feed.ugc?.likeCount = it - 1
                    }
                }
                feed.ugc?.notifyChange()
                FlowBus.post(DATA_FROM_INTERACTION, feed)
            }
        }
    }

    @JvmStatic
    fun openShareDialog(owner: LifecycleOwner, feed: Feed) {
        val url = "http://h5.aliyun.ppkoke.com/item/%s?timestamp=%s&user_id=%s";
        val format = url.format(feed.itemId, Date().time, UserManager.userId())
        ShareDialog(owner as Context).apply {
            setShareContent(format)
            setShareItemClickListener {
                owner.lifecycleScope.launch {
                    val res = prepare { apiService.increaseShareCount(feed.itemId ?: 0L) }
                    if (res.isSuccess) {
                        res.getOrNull()?.count?.let {
                            feed.ugc?.shareCount = it
                            feed.ugc?.notifyChange()
                        }
                    }
                    hide()
                    toast("分享成功")
                }
            }
        }.show()
    }

    @JvmStatic
    fun toggleCommentLiked(owner: LifecycleOwner, comment: Comment) {
        doAfterLogin {
            val res = prepare { apiService.toggleCommentLike(comment.commentId ?: 0) }
            if (res.isSuccess) {
                val hasLiked = res.getOrNull()?.hasLiked ?: false
                comment.ugc?.hasdiss = hasLiked
                comment.ugc?.notifyChange()
            }
        }
    }

    @JvmStatic
    fun doAfterLogin(func: suspend CoroutineScope.() -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            if (!UserManager.isLogin()) {
                UserManager.login(AppGlobals.application).collectLatest {
                    if (UserManager.isLogin(it)) {
                        func()
                        this.cancel()
                    }
                }
            } else {
                func()
                this.cancel()
            }
        }
    }
}