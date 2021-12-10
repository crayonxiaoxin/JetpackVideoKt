package com.github.crayonxiaoxin.ppjoke_kt.model

data class Destination(
    var id: Int,
    var pageUrl: String,
    var clazzName: String,
    var needLogin: Boolean,
    var isFragment: Boolean,
    var asStarter: Boolean,
)