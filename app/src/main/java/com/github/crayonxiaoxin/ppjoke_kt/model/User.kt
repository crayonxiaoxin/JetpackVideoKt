package com.github.crayonxiaoxin.ppjoke_kt.model

import androidx.databinding.BaseObservable
import java.io.Serializable

data class User(
    var avatar: String?,
    var commentCount: Int?,
    var description: String?,
    var expires_time: Long?,
    var favoriteCount: Int?,
    var feedCount: Int?,
    var followCount: Int?,
    var followerCount: Int?,
    var hasFollow: Boolean?,
    var historyCount: Int?,
    var id: Int?,
    var likeCount: Int?,
    var name: String?,
    var qqOpenId: String?,
    var score: Int?,
    var topCommentCount: Int?,
    var userId: Long?
) : BaseObservable(), Serializable