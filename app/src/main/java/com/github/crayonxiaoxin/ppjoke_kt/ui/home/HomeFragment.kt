package com.github.crayonxiaoxin.ppjoke_kt.ui.home

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.crayonxiaoxin.lib_nav_annotation.FragmentDestination
import com.github.crayonxiaoxin.ppjoke_kt.model.Feed
import com.github.crayonxiaoxin.ppjoke_kt.base.AbsListFragment
import kotlinx.coroutines.launch

@FragmentDestination("main/tabs/home", asStarter = true)
class HomeFragment() : AbsListFragment<Feed, HomeViewModel, FeedAdapter>() {
    private val KEY_FEED_TYPE = "key_fed_type"

    override val viewModel: HomeViewModel by viewModels()
    override val adapter: FeedAdapter = FeedAdapter()

    fun newInstance(feedType: String): HomeFragment {
        val args = Bundle()
        args.putString(KEY_FEED_TYPE, feedType)
        val fragment = HomeFragment()
        fragment.arguments = args
        return fragment
    }

    override fun afterCreateView() {
        viewModel.feedType = arguments?.getString(KEY_FEED_TYPE) ?: "all"
        adapter.setOnItemClickListener {
            lifecycleScope.launch {
                adapter.update(it.copy(feeds_text = it.feeds_text + " haha"))
            }
        }
    }
}