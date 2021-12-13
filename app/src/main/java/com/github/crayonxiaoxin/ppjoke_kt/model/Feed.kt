package com.github.crayonxiaoxin.ppjoke_kt.model

data class Feed(
    var activityIcon: String?,
    var activityText: String?,
    var author: User?,
    var authorId: Int?,
    var cover: String?,
    var createTime: Long?,
    var duration: Double?,
    var feeds_text: String?,
    var height: Int?,
    var id: Int?,
    var itemId: Long?,
    var itemType: Int?,
    var topComment: Any?,
    var ugc: Ugc?,
    var url: String?,
    var width: Int?
)