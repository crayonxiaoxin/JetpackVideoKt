package com.github.crayonxiaoxin.ppjoke_kt.ui.find

import android.os.Bundle
import androidx.fragment.app.viewModels
import com.github.crayonxiaoxin.lib_common.global.toast
import com.github.crayonxiaoxin.ppjoke_kt.R
import com.github.crayonxiaoxin.ppjoke_kt.base.AbsListFragment
import com.github.crayonxiaoxin.ppjoke_kt.model.TagList

class TagListFragment : AbsListFragment<TagList, TagListViewModel, TagListAdapter>() {
    override val viewModel: TagListViewModel by viewModels()
    override fun initAdapter(): TagListAdapter {
        viewModel.tagType = arguments?.getString(KEY_TAG_TYPE, "") ?: ""
        return TagListAdapter()
    }

    override fun afterCreateView() {
        if (viewModel.tagType == "onlyFollow") {
            emptyView.setTitle(getString(R.string.tag_list_no_follow))
            emptyView.setButton(getString(R.string.tag_list_no_follow_button)) {
                // 因为 flow 只发送不同的值
                viewModel.switchTabFlow().value += 1
            }
        }
        adapter.setOnItemClickListener {
            toast("开发中: ${it.title}")
        }
        adapter.setOnFollowClickListener {
            viewModel.toggleTagFollow(it)
        }
    }

    companion object {
        val KEY_TAG_TYPE = "key_tag_type"
        fun newInstance(tagType: String): TagListFragment {
            val args = Bundle()
            args.putString(KEY_TAG_TYPE, tagType)
            val fragment = TagListFragment()
            fragment.arguments = args
            return fragment
        }
    }
}