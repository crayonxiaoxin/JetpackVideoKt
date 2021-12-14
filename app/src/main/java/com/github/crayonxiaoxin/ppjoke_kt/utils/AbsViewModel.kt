package com.github.crayonxiaoxin.ppjoke_kt.utils

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.github.crayonxiaoxin.ppjoke_kt.model.Feed
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

abstract class AbsViewModel<T : Any> : ViewModel() {

    // 因为 MutableLiveData 只要改变 value 就会触发，而 MutableStateFlow 必须要数据不相同（!equals）才会触发
    var currentRes: MutableLiveData<PagingData<T>?> = MutableLiveData()

    fun update(item: T) {
        viewModelScope.launch {
            val newData = currentRes.value?.map {
                updateBy(it, item)
            }
            currentRes.value = newData
        }
    }

    abstract fun updateBy(old: T, new: T): T

    fun delete(item: T) {
        viewModelScope.launch {
            val newData = currentRes.value?.filter {
                deleteBy(it, item)
            }
            currentRes.value = newData
        }
    }

    abstract fun deleteBy(old: T, new: T): Boolean


    fun add(item: T, footer: Boolean = true) {
        viewModelScope.launch {
            val newData = if (footer) {
                currentRes.value?.insertFooterItem(item = item)
            } else {
                currentRes.value?.insertHeaderItem(item = item)
            }
            currentRes.value = newData
        }
    }

}