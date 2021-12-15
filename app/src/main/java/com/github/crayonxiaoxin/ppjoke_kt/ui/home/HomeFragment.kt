package com.github.crayonxiaoxin.ppjoke_kt.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.crayonxiaoxin.lib_common.global.toast
import com.github.crayonxiaoxin.lib_nav_annotation.FragmentDestination
import com.github.crayonxiaoxin.ppjoke_kt.databinding.FragmentHomeBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@FragmentDestination("main/tabs/home", asStarter = true)
class HomeFragment : Fragment() {
    private val KEY_FEED_TYPE = "key_fed_type"

    private var feedType: String = ""
    private var adapter: FeedAdapter = FeedAdapter()
    private val viewModel: HomeViewModel by viewModels()

    fun newInstance(feedType: String): HomeFragment {
        val args = Bundle()
        args.putString(KEY_FEED_TYPE, feedType)
        val fragment = HomeFragment()
        fragment.arguments = args
        return fragment
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        feedType = arguments?.getString(KEY_FEED_TYPE) ?: "all"
        val binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.recyclerView.adapter = adapter
        lifecycleScope.launch {
            viewModel.getFeedList(feedType).collect {
                adapter.submit(it)
            }
        }
        adapter.setOnItemClickListener {
            lifecycleScope.launch {
                adapter.update(it.copy(feeds_text = it.feeds_text + " haha"))
            }
        }
        binding.refreshLayout.setOnRefreshListener { adapter.refresh() }
        adapter.addLoadStateListener {
            Log.e("TAG", "onCreateView: $it")
            when (it.refresh) {
                is LoadState.NotLoading, is LoadState.Error -> binding.refreshLayout.finishRefresh()
                else -> {
                    toast("refresh")
                }
            }
        }
        return binding.root
    }
}