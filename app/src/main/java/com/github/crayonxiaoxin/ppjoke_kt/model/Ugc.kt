package com.github.crayonxiaoxin.ppjoke_kt.model

import androidx.databinding.BaseObservable
import java.io.Serializable

data class Ugc(
    var commentCount: Int?,
    var hasDissed: Boolean?,
    var hasFavorite: Boolean?,
    var hasLiked: Boolean?,
    var hasdiss: Boolean?,
    var likeCount: Int?,
    var shareCount: Int?
) : BaseObservable(), Serializable