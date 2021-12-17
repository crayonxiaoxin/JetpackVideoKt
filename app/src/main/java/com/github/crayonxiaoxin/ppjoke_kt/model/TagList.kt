package com.github.crayonxiaoxin.ppjoke_kt.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import java.io.Serializable

data class TagList(
    var id: Int,
    var tagId: Long,
    var title: String,
    var icon: String?,
    var activityIcon: String?,
    var background: String?,
    var enterNum: Int?,
    var feedNum: Int?,
    var followNum: Int?,
    @Bindable var hasFollow: Boolean?,
    var intro: String?,
) : BaseObservable(), Serializable