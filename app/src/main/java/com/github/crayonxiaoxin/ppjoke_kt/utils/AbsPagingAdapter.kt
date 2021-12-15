package com.github.crayonxiaoxin.ppjoke_kt.utils

import android.annotation.SuppressLint
import androidx.lifecycle.Lifecycle
import androidx.paging.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class AbsPagingAdapter<T : Any, VH : RecyclerView.ViewHolder>(var diffCallback: DiffUtil.ItemCallback<T>) :
    PagingDataAdapter<T, VH>(diffCallback) {

    private var pagingData: PagingData<T>? = null

    suspend fun submit(pagingData: PagingData<T>) {
        this.pagingData = pagingData
        submitData(pagingData)
    }

    fun submit(lifecycle: Lifecycle, pagingData: PagingData<T>) {
        this.pagingData = pagingData
        submitData(lifecycle, pagingData)
    }

    suspend fun update(item: T) {
        this.pagingData?.map {
            if (diffCallback.areItemsTheSame(it, item)) {
                item
            } else {
                it
            }
        }?.let { submit(it) }
    }

    suspend fun delete(item: T) {
        this.pagingData?.filter {
            diffCallback.areItemsTheSame(it, item)
        }?.let { submit(it) }
    }

    @SuppressLint("CheckResult")
    suspend fun add(item: T, footer: Boolean = true) {
        if (footer) {
            this.pagingData?.insertFooterItem(item = item)?.let { submit(it) }
        } else {
            this.pagingData?.insertHeaderItem(item = item)?.let { submit(it) }
        }
    }

}