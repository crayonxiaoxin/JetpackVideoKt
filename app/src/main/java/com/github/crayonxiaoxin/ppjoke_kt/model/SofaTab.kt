package com.github.crayonxiaoxin.ppjoke_kt.model

data class SofaTab(
    var activeColor: String,
    var activeSize: Int,
    var normalColor: String,
    var normalSize: Int,
    var select: Int,
    var tabGravity: Int,
    var tabs: List<Tab>
) {
    data class Tab(
        var enable: Boolean,
        var index: Int,
        var tag: String,
        var title: String
    )
}