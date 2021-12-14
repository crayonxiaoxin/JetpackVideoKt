package com.github.crayonxiaoxin.ppjoke_kt.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.github.crayonxiaoxin.ppjoke_kt.model.Feed
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    private var feedType: String? = ""
    private var currentRes: Flow<PagingData<Feed>>? = null

    fun getFeedList(feedType: String?): Flow<PagingData<Feed>>? {
        this.feedType = feedType
        currentRes = Repository.getFeedList(feedType).cachedIn(viewModelScope)
        viewModelScope.launch { Log.e("TAG", "getFeedList: ${currentRes?.first()?.toString()}", ) }
        return currentRes
    }
}