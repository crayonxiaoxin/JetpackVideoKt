package com.github.crayonxiaoxin.ppjoke_kt.exoplayer

import android.view.ViewGroup

interface IPlayTarget {
    fun getOwner(): ViewGroup
    fun onActive()
    fun inActive()
    fun isPlaying(): Boolean
}