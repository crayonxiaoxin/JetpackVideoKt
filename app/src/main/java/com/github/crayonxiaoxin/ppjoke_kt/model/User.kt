package com.github.crayonxiaoxin.ppjoke_kt.model

data class User(
    var id: Int,
    var userId: Int,
    var avatar: String?=null,
    var commentCount: Int?=null,
    var description: String?=null,
    var expires_time: Long?=null,
    var favoriteCount: Int?=null,
    var feedCount: Int?=null,
    var followCount: Int?=null,
    var followerCount: Int?=null,
    var hasFollow: Boolean?=null,
    var historyCount: Int?=null,
    var likeCount: Int?=null,
    var name: String?=null,
    var qqOpenId: String?=null,
    var score: Int?=null,
    var topCommentCount: Int?=null,
)