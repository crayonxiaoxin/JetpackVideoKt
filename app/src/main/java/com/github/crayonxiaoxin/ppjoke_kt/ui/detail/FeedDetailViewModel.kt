package com.github.crayonxiaoxin.ppjoke_kt.ui.detail

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.github.crayonxiaoxin.ppjoke_kt.base.AbsViewModel
import com.github.crayonxiaoxin.ppjoke_kt.base.prepare
import com.github.crayonxiaoxin.ppjoke_kt.model.Comment
import com.github.crayonxiaoxin.ppjoke_kt.utils.apiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class FeedDetailViewModel : AbsViewModel<Comment>() {

    var itemId: Long = 0L

    override fun getList(): Flow<PagingData<Comment>> {
        return Pager(
            config = PagingConfig(pageSize = 10, enablePlaceholders = false, initialLoadSize = 10),
            pagingSourceFactory = { CommentPagingSource(itemId) }
        ).flow.cachedIn(viewModelScope)
    }

    fun deleteComment(comment: Comment, result: suspend CoroutineScope.(success: Boolean) -> Unit) {
        viewModelScope.launch {
            val res = prepare { apiService.deleteComment(itemId, comment.commentId) }
            result(res.isSuccess && (res.getOrNull()?.result ?: false))
        }
    }
}