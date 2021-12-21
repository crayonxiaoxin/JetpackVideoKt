package com.github.crayonxiaoxin.ppjoke_kt.ui

import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.github.crayonxiaoxin.lib_common.extension.FlowBus
import com.github.crayonxiaoxin.lib_common.global.AppGlobals
import com.github.crayonxiaoxin.lib_common.global.toast
import com.github.crayonxiaoxin.ppjoke_kt.base.prepare
import com.github.crayonxiaoxin.ppjoke_kt.model.Comment
import com.github.crayonxiaoxin.ppjoke_kt.model.Feed
import com.github.crayonxiaoxin.ppjoke_kt.model.TagList
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
                if (feed.ugc?.hasLiked == hasLiked) return@doAfterLogin
                feed.ugc?.hasLiked = hasLiked
                if (hasLiked) {
                    feed.ugc?.hasdiss = false
                    feed.ugc?.likeCount?.let {
                        feed.ugc?.likeCount = it + 1
                    }
                } else {
                    feed.ugc?.likeCount?.let {
                        feed.ugc?.likeCount = it - 1
                    }
                }
                feed.ugc?.notifyChange()
                Log.e("TAG", "toggleFeedLiked: ${feed.hashCode()}")
                notify(feed)
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
                notify(feed)
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
    fun toggleFeedFavorite(owner: LifecycleOwner?, feed: Feed) {
        doAfterLogin {
            val res = prepare { apiService.toggleFavorite(feed.itemId ?: 0L) }
            if (res.isSuccess) {
                val hasFavorite = res.getOrNull()?.hasFavorite ?: false
                feed.ugc?.hasFavorite = hasFavorite
                feed.ugc?.notifyChange()
                notify(feed)
            }
        }
    }

    @JvmStatic
    fun toggleCommentLiked(owner: LifecycleOwner?, comment: Comment) {
        doAfterLogin {
            val res = prepare { apiService.toggleCommentLike(comment.commentId ?: 0) }
            if (res.isSuccess) {
                val hasLiked = res.getOrNull()?.hasLiked ?: false
                comment.ugc?.hasLiked = hasLiked
                comment.ugc?.likeCount?.let {
                    if (hasLiked) {
                        comment.ugc?.likeCount = it + 1
                    } else {
                        comment.ugc?.likeCount = it - 1
                    }
                }
                comment.ugc?.notifyChange()
            }
        }
    }

    @JvmStatic
    fun toggleTagLiked(owner: LifecycleOwner?, tagList: TagList) {
        doAfterLogin {
            val res = prepare { apiService.toggleTagFollow(tagList.tagId) }
            if (res.isSuccess) {
                res.getOrNull()?.hasFollow?.let {
                    tagList.hasFollow = it
                    tagList.notifyChange()
                }
            }
        }
    }

    @JvmStatic
    fun toggleFollowUser(owner: LifecycleOwner?, feed: Feed) {
        doAfterLogin {
            val res = prepare { apiService.toggleUserFollow(feed.authorId ?: 0) }
            if (res.isSuccess) {
                res.getOrNull()?.hasLiked?.let {
                    feed.author?.hasFollow = it
                    feed.author?.notifyChange()
                    notify(feed)
                }
            }
        }
    }

    suspend fun notify(feed: Feed) {
        FlowBus.post(DATA_FROM_INTERACTION, feed)
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