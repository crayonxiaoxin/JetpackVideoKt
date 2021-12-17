package com.github.crayonxiaoxin.ppjoke_kt.ui.find

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.github.crayonxiaoxin.ppjoke_kt.base.prepare
import com.github.crayonxiaoxin.ppjoke_kt.model.TagList
import com.github.crayonxiaoxin.ppjoke_kt.utils.apiService

class TagListPagingSource(var tagType: String = "") : PagingSource<Long, TagList>() {
    private var offset = 0
    override fun getRefreshKey(state: PagingState<Long, TagList>): Long {
        return 0L
    }

    override suspend fun load(params: LoadParams<Long>): LoadResult<Long, TagList> {
        val position = params.key ?: 0L
        return if (position < 0L) {
            LoadResult.Page(ArrayList(), -1L, 0L)
        } else {
            val result = prepare {
                apiService.queryTagList(
                    position, tagType, offset, pageCount = params.loadSize
                )
            }
            if (result.isSuccess) {
                val data = result.getOrDefault(ArrayList())
                offset += data.size
                val nextKey = if (data.isEmpty()) null else data.last().tagId
                LoadResult.Page(data, null, nextKey)
            } else {
                LoadResult.Error(result.exceptionOrNull()!!)
            }
        }
    }
}