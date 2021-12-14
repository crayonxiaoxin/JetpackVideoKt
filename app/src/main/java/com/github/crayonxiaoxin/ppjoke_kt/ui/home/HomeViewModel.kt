package com.github.crayonxiaoxin.ppjoke_kt.ui.home

import androidx.lifecycle.viewModelScope
import androidx.paging.*
import com.github.crayonxiaoxin.ppjoke_kt.model.Feed
import com.github.crayonxiaoxin.ppjoke_kt.utils.AbsViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class HomeViewModel : AbsViewModel<Feed>() {
    var feedType: String? = ""

    fun getFeedList(feedType: String?) {
        this.feedType = feedType
        viewModelScope.launch {
            currentRes.value =
                Repository.getFeedList(feedType).cachedIn(viewModelScope).firstOrNull()
        }
    }

    override fun updateBy(old: Feed, new: Feed): Feed {
        return if (old.itemId == new.itemId) new else old
    }

    override fun deleteBy(old: Feed, new: Feed): Boolean {
        return old.itemId == new.itemId
    }

}