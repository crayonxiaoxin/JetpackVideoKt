package com.github.crayonxiaoxin.ppjoke_kt.ui.home

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.github.crayonxiaoxin.ppjoke_kt.model.Feed
import kotlinx.coroutines.flow.Flow

object Repository {
    fun getFeedList(feedType: String? = ""): Flow<PagingData<Feed>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false,
                initialLoadSize = 10
            ),
            pagingSourceFactory = { FeedPagingSource(feedType) }
        ).flow
    }
}