package com.github.crayonxiaoxin.ppjoke_kt.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.crayonxiaoxin.lib_common.view.EmptyView
import com.github.crayonxiaoxin.ppjoke_kt.databinding.LayoutListBinding
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

abstract class AbsListFragment<T : Any, VM : AbsViewModel<T>, Adapter : AbsPagingAdapter<T, out RecyclerView.ViewHolder>> :
    Fragment() {

    protected lateinit var refreshLayout: SmartRefreshLayout
    protected lateinit var recyclerView: RecyclerView
    protected lateinit var emptyView: EmptyView

    abstract val adapter: Adapter
    abstract val viewModel: VM

//    // 通过反射实例化 viewModel
//    private fun initViewModel() {
//        val genericSuperclass = javaClass.genericSuperclass as ParameterizedType
//        val actualTypeArguments = genericSuperclass.actualTypeArguments
//        if (actualTypeArguments.size > 1) {
//            val clazz = (actualTypeArguments[1] as Class<*>).asSubclass(AbsViewModel::class.java)
//            viewModel = ViewModelProvider(this)[clazz] as VM
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = LayoutListBinding.inflate(inflater, container, false)
        recyclerView = binding.recyclerView
        refreshLayout = binding.refreshLayout
        emptyView = binding.emptyView

        recyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        recyclerView.adapter = adapter

        refreshLayout.setOnRefreshListener { adapter.refresh() }
        adapter.addLoadStateListener {
            when (it.refresh) {
                is LoadState.NotLoading, is LoadState.Error -> {
                    refreshLayout.finishRefresh()
                    if (adapter.itemCount == 0) {
                        emptyView.visibility = View.VISIBLE
                    } else {
                        emptyView.visibility = View.GONE
                    }
                }
                else -> {
                }
            }
        }
        afterCreateView()
        return binding.root
    }

    open fun afterCreateView() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            viewModel.getList().collect {
                adapter.submit(it)
            }
        }
    }
}