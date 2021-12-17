package com.github.crayonxiaoxin.ppjoke_kt.ui.find

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.crayonxiaoxin.lib_nav_annotation.FragmentDestination
import com.github.crayonxiaoxin.ppjoke_kt.model.SofaTab
import com.github.crayonxiaoxin.ppjoke_kt.ui.sofa.SofaFragment
import com.github.crayonxiaoxin.ppjoke_kt.utils.AppConfig
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@FragmentDestination("main/tabs/find")
class FindFragment : SofaFragment() {
    override fun getTabs(): SofaTab {
        return AppConfig.getFindTabConfig()
    }

    override fun generateFragment(position: Int): Fragment {
        return TagListFragment.newInstance(tabs[position].tag)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.addFragmentOnAttachListener { fragmentManager, fragment ->
            val tag = fragment.arguments?.getString(TagListFragment.KEY_TAG_TYPE)
            Log.e("TAG", "onViewCreated: $tag")
            if (tag == "onlyFollow") {
                lifecycleScope.launch {
                    val tagListViewModel by viewModels<TagListViewModel>({ fragment })
                    tagListViewModel.switchTabFlow().collectLatest {
                        Log.e("TAG", "onViewCreated: $it")
                        if (it >= 0) {
                            viewPager.currentItem = 1
                        }
                    }
                }
            }
        }
    }
}