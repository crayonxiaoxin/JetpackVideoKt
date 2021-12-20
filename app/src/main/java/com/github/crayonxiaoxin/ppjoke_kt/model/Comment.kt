package com.github.crayonxiaoxin.ppjoke_kt.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import java.io.Serializable

data class Comment(
    @Bindable var author: User?,
    var commentCount: Int?,
    var commentId: Long,
    var commentText: String?,
    var commentType: Int?,
    var createTime: Long?,
    var hasLiked: Boolean?,
    var height: Int?,
    var id: Int,
    var imageUrl: String?,
    var itemId: Long,
    var likeCount: Int?,
    @Bindable var ugc: Ugc?,
    var userId: Int?,
    var videoUrl: String?,
    var width: Int?
) : BaseObservable(), Serializable