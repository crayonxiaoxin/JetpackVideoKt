package com.github.crayonxiaoxin.ppjoke_kt.model

data class Comment(
    var author: User?,
    var commentCount: Int?,
    var commentId: Long?,
    var commentText: String?,
    var commentType: Int?,
    var createTime: Long?,
    var hasLiked: Boolean?,
    var height: Int?,
    var id: Int?,
    var imageUrl: String?,
    var itemId: Int?,
    var likeCount: Int?,
    var ugc: Ugc?,
    var userId: Int?,
    var videoUrl: String?,
    var width: Int?
)