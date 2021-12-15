package com.github.crayonxiaoxin.ppjoke_kt.ui.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.paging.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class AbsPagingAdapter<T : Any, VH : RecyclerView.ViewHolder>(diffCallback: DiffUtil.ItemCallback<T>) :
    PagingDataAdapter<T, VH>(diffCallback) {
    protected var pagingData: PagingData<T>? = null
    suspend fun submit(pagingData: PagingData<T>) {
        Log.e("TAG", "submit: $pagingData" )
        this.pagingData = pagingData
        submitData(pagingData)
    }

    fun submit(lifecycle: Lifecycle, pagingData: PagingData<T>) {
        this.pagingData = pagingData
        submitData(lifecycle, pagingData)
    }

    abstract fun updateBy(old: T, new: T): Boolean

    suspend fun update(item: T) {
        this.pagingData?.map {
            if (updateBy(it, item)) {
                item
            } else {
                it
            }
        }?.let { submit(it) }
    }

    suspend fun delete(item: T) {
        this.pagingData?.filter {
            item == it
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