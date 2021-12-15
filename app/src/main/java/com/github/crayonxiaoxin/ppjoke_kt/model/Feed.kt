package com.github.crayonxiaoxin.ppjoke_kt.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import java.io.Serializable

data class Feed(
    var activityIcon: String?,
    var activityText: String?,
    @Bindable var author: User?,
    var authorId: Int?,
    var cover: String?,
    var createTime: Long?,
    var duration: Double?,
    var feeds_text: String?,
    var height: Int?,
    var id: Int?,
    var itemId: Long?,
    var itemType: Int?,
    @Bindable var topComment: Comment?,
    @Bindable var ugc: Ugc?,
    var url: String?,
    var width: Int?
) : BaseObservable(), Serializable