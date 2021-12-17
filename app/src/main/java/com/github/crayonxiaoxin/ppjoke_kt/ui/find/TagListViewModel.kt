package com.github.crayonxiaoxin.ppjoke_kt.ui.find

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.github.crayonxiaoxin.ppjoke_kt.base.AbsViewModel
import com.github.crayonxiaoxin.ppjoke_kt.base.prepare
import com.github.crayonxiaoxin.ppjoke_kt.model.TagList
import com.github.crayonxiaoxin.ppjoke_kt.ui.InteractionPresenter
import com.github.crayonxiaoxin.ppjoke_kt.utils.apiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class TagListViewModel : AbsViewModel<TagList>() {
    var tagType: String = ""
    override fun getList(): Flow<PagingData<TagList>> {
        return Pager(
            config = PagingConfig(100, initialLoadSize = 100),
            pagingSourceFactory = { TagListPagingSource(tagType) }
        ).flow.cachedIn(viewModelScope)
    }

    fun toggleTagFollow(tagList: TagList) {
        InteractionPresenter.toggleTagLiked(null,tagList)
    }

    private val switchTab: MutableStateFlow<Int> = MutableStateFlow(-1)
    fun switchTabFlow(): MutableStateFlow<Int> {
        return switchTab
    }
}