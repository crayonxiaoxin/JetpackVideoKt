package com.github.crayonxiaoxin.ppjoke_kt.ui.publish

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.crayonxiaoxin.lib_common.global.toast
import com.github.crayonxiaoxin.lib_common.utils.PixUtils
import com.github.crayonxiaoxin.ppjoke_kt.R
import com.github.crayonxiaoxin.ppjoke_kt.model.TagList
import com.github.crayonxiaoxin.ppjoke_kt.ui.find.TagListViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TabBottomSheetFragment : BottomSheetDialogFragment() {
    private val viewModel: TagListViewModel by viewModels()
    private val tagsAdapter = TagsAdapter()
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        val view: View =
            layoutInflater.inflate(R.layout.layout_tag_bottom_sheet_dialog, null, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = tagsAdapter

        dialog.setContentView(view)
        val dialogParent = view.parent as ViewGroup
        val behavior = BottomSheetBehavior.from(dialogParent)
        val screenHeight = PixUtils.getScreenHeight()
        behavior.peekHeight = screenHeight / 3 // 最小高度
        behavior.isHideable = false // 下滑是否隐藏

        val layoutParams = dialogParent.layoutParams
        layoutParams.height = screenHeight / 3 * 2 // 最大高度
        dialogParent.layoutParams = layoutParams

        queryTagList()

        return dialog
    }

    private fun queryTagList() {
        lifecycleScope.launch {
            viewModel.getList().collect {
                tagsAdapter.submit(it)
            }
        }
        tagsAdapter.addLoadStateListener {
            if (it.refresh is LoadState.Error) {
                toast("加载失败")
            }
        }
        tagsAdapter.setOnTagItemSelected {
            mListener?.invoke(it)
            dismiss()
        }
    }

    private var mListener: ((TagList) -> Unit)? = null
    fun setOnTagItemSelected(listener: (TagList) -> Unit) {
        this.mListener = listener
    }
}