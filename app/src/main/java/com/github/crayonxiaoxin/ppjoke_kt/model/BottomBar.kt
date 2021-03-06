package com.github.crayonxiaoxin.ppjoke_kt.model

data class BottomBar(
    var activeColor: String,
    var inActiveColor: String,
    var selectTab: Int,
    var tabs: List<Tab>
) {
    data class Tab(
        var enable: Boolean,
        var index: Int,
        var pageUrl: String,
        var size: Int,
        var tintColor: String?,
        var title: String
    )
}