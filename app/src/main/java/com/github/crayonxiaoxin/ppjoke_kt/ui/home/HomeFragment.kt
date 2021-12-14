package com.github.crayonxiaoxin.ppjoke_kt.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.crayonxiaoxin.lib_nav_annotation.FragmentDestination
import com.github.crayonxiaoxin.ppjoke_kt.databinding.FragmentHomeBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@FragmentDestination("main/tabs/home", asStarter = true)
class HomeFragment : Fragment() {
    private var adapter: FeedAdapter = FeedAdapter()
    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.recyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        binding.recyclerView.adapter = adapter
        lifecycleScope.launch {
            viewModel.getFeedList("all")?.collect {
                adapter.submitData(lifecycle, it)
            }
        }
        return binding.root
    }
}