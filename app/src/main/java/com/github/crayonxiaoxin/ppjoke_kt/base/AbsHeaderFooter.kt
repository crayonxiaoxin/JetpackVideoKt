package com.github.crayonxiaoxin.ppjoke_kt.base

import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * paging 通用 header、footer
 */
abstract class AbsHeaderFooter :
    LoadStateAdapter<RecyclerView.ViewHolder>() {
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, loadState: LoadState) {

    }

    // 为了在任何状态下都显示 header & footer
    override fun displayLoadStateAsItem(loadState: LoadState): Boolean {
        return true
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): RecyclerView.ViewHolder {
        val view = setView(parent)
        return object : RecyclerView.ViewHolder(view) {}
    }

    abstract fun setView(parent: ViewGroup): View
}