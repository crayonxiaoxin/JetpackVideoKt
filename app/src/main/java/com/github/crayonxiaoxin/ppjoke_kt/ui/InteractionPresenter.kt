package com.github.crayonxiaoxin.ppjoke_kt.ui

import androidx.lifecycle.LifecycleOwner
import com.github.crayonxiaoxin.lib_common.extension.FlowBus
import com.github.crayonxiaoxin.lib_common.global.AppGlobals
import com.github.crayonxiaoxin.ppjoke_kt.base.prepare
import com.github.crayonxiaoxin.ppjoke_kt.model.Comment
import com.github.crayonxiaoxin.ppjoke_kt.model.Feed
import com.github.crayonxiaoxin.ppjoke_kt.utils.UserManager
import com.github.crayonxiaoxin.ppjoke_kt.utils.apiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

object InteractionPresenter {
    const val DATA_FROM_INTERACTION = "data_from_interaction"

    @JvmStatic
    fun toggleFeedLiked(owner: LifecycleOwner, feed: Feed) {
        doAfterLogin {
            val res = prepare { apiService.toggleFeedLike(feed.itemId ?: 0) }
            if (res.isSuccess) {
                val hasLiked = res.getOrNull()?.hasLiked ?: false
                feed.ugc?.hasLiked = hasLiked
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
                feed.ugc?.notifyChange()
                FlowBus.post(DATA_FROM_INTERACTION, feed)
            }
        }
    }

    @JvmStatic
    fun openShareDialog(owner: LifecycleOwner, feed: Feed) {

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
    fun doAfterLogin(func: suspend () -> Unit) {
        CoroutineScope(Dispatchers.Main).launch {
            if (!UserManager.isLogin()) {
                val login = UserManager.login(AppGlobals.application)
                CoroutineScope(Dispatchers.Main).launch {
                    login.collectLatest {
                        if (UserManager.isLogin(it)) {
                            func()
                            this.cancel()
                        }
                    }
                }
            } else {
                func()
            }
        }
    }
}