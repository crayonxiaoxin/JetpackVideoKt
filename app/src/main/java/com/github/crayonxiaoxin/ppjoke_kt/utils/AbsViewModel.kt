package com.github.crayonxiaoxin.ppjoke_kt.utils

import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

abstract class AbsViewModel<T : Any> : ViewModel() {
    abstract fun getList(): Flow<PagingData<T>>
}