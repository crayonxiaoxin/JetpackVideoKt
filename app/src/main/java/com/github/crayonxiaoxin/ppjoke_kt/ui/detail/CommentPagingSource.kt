package com.github.crayonxiaoxin.ppjoke_kt.ui.detail

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.crayonxiaoxin.ppjoke_kt.base.prepare
import com.github.crayonxiaoxin.ppjoke_kt.model.Comment
import com.github.crayonxiaoxin.ppjoke_kt.utils.apiService

class CommentPagingSource(private val itemId: Long) : PagingSource<Int, Comment>() {
    override fun getRefreshKey(state: PagingState<Int, Comment>): Int? {
        return 0
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Comment> {
        val id = params.key ?: 0
        return if (id < 0) {
            LoadResult.Page(ArrayList(), -1, 0)
        } else {
            val result = prepare { apiService.queryFeedComments(id, itemId, params.loadSize) }
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