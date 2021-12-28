package com.github.crayonxiaoxin.ppjoke_kt.ui.my

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.crayonxiaoxin.ppjoke_kt.base.prepare
import com.github.crayonxiaoxin.ppjoke_kt.model.Feed
import com.github.crayonxiaoxin.ppjoke_kt.utils.apiService

class ProfileFeedPagingSource(private val profileType: String?) : PagingSource<Int, Feed>() {

    override fun getRefreshKey(state: PagingState<Int, Feed>): Int {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Feed> {
        val position = params.key ?: 0
        return if (position < 0) {
            LoadResult.Page(ArrayList(), -1, 0)
        } else {
            val result = prepare {
                apiService.queryProfileFeeds(position, profileType ?: "", pageCount = params.loadSize)
            }
            if (result.isSuccess) {
                val data = result.getOrDefault(ArrayList())
                val nextKey = if (data.isEmpty()) null else data.last().id
                LoadResult.Page(data, null, nextKey)
            } else {
                LoadResult.Error(result.exceptionOrNull()!!)
            }
        }
    }

}