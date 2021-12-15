package com.github.crayonxiaoxin.ppjoke_kt.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.github.crayonxiaoxin.ppjoke_kt.model.Feed
import kotlinx.coroutines.flow.Flow

class HomeViewModel : ViewModel() {
    var feedType: String? = ""

    fun getFeedList(feedType: String?): Flow<PagingData<Feed>> {
        this.feedType = feedType
        // 这里一定要使用这个 pager.flow 才能被观察到后续变化
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                initialLoadSize = 10
            ),
            pagingSourceFactory = { FeedPagingSource(feedType) }
        ).flow.cachedIn(viewModelScope)
    }
}