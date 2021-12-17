package com.github.crayonxiaoxin.ppjoke_kt.ui.find

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.crayonxiaoxin.lib_common.utils.StatusBar
import com.github.crayonxiaoxin.lib_common.utils.dp
import com.github.crayonxiaoxin.lib_common.view.EmptyView
import com.github.crayonxiaoxin.ppjoke_kt.R
import com.github.crayonxiaoxin.ppjoke_kt.base.AbsHeaderFooter
import com.github.crayonxiaoxin.ppjoke_kt.databinding.ActivityTagFeedListBinding
import com.github.crayonxiaoxin.ppjoke_kt.databinding.LayoutTagFeedListHeaderBinding
import com.github.crayonxiaoxin.ppjoke_kt.exoplayer.PageListPlayDetector
import com.github.crayonxiaoxin.ppjoke_kt.exoplayer.PageListPlayManager
import com.github.crayonxiaoxin.ppjoke_kt.model.Feed
import com.github.crayonxiaoxin.ppjoke_kt.model.TagList
import com.github.crayonxiaoxin.ppjoke_kt.ui.home.FeedAdapter
import com.github.crayonxiaoxin.ppjoke_kt.ui.home.HomeViewModel
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TagFeedListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTagFeedListBinding
    private lateinit var refreshLayout: SmartRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyView: EmptyView
    private val viewModel: HomeViewModel by viewModels()
    private var playDetector: PageListPlayDetector? = null
    private lateinit var adapter: FeedAdapter
    private var tagList: TagList? = null
    private var shouldPause: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        StatusBar.fitSystemBar(this, false)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_tag_feed_list)
        recyclerView = binding.refreshLayoutView.recyclerView
        refreshLayout = binding.refreshLayoutView.refreshLayout
        emptyView = binding.refreshLayoutView.emptyView

        this.tagList = intent.getSerializableExtra(KEY_TAG_LIST) as TagList?
        if (tagList == null) finish()
        binding.tagList = tagList

        playDetector = PageListPlayDetector(this, recyclerView)

        adapter = initAdapter()
        recyclerView.layoutManager =
            LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        val concatAdapter = wrapAdapter() // 添加 header
        recyclerView.adapter = concatAdapter

        // 分割线
        ContextCompat.getDrawable(this, R.drawable.list_divider)?.let {
            val dividerItemDecoration =
                DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
            dividerItemDecoration.setDrawable(it)
            recyclerView.addItemDecoration(dividerItemDecoration)
        }

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
        adapter.setOnItemClickListener {
            // 如果是视频，不需要暂停
            shouldPause = it.itemType != Feed.TYPE_VIDEO
            lifecycleScope.launch {
//                adapter.update(it.copy(feeds_text = it.feeds_text + " haha"))
            }
        }

        lifecycleScope.launch {
            viewModel.getList().collect {
                adapter.submit(it)
            }
        }
    }

    private var totalScrollY = 0
    private val criticalValue = 48.dp
    private fun wrapAdapter(): ConcatAdapter {
        val headerAdapter = object : AbsHeaderFooter() {
            override fun setView(parent: ViewGroup): View {
                val headerBinding = LayoutTagFeedListHeaderBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ).apply {
                    this.owner = this@TagFeedListActivity
                    this.tagList = this@TagFeedListActivity.tagList
                }
                return headerBinding.root
            }
        }
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                totalScrollY += dy
                if (totalScrollY > criticalValue) {
                    binding.tagLogo.visibility = View.VISIBLE
                    binding.tagTitle.visibility = View.VISIBLE
                    binding.topBarFollow.visibility = View.VISIBLE
                    binding.topLine.visibility = View.VISIBLE
                    binding.actionBack.setImageResource(R.drawable.icon_back_black)
                    binding.topBar.setBackgroundColor(Color.WHITE)
                } else {
                    binding.tagLogo.visibility = View.GONE
                    binding.tagTitle.visibility = View.GONE
                    binding.topBarFollow.visibility = View.GONE
                    binding.topLine.visibility = View.GONE
                    binding.actionBack.setImageResource(R.drawable.icon_back_white)
                    binding.topBar.background = ColorDrawable(Color.TRANSPARENT)
                }
            }
        })
        return adapter.withLoadStateHeader(headerAdapter)
    }

    private fun initAdapter(): FeedAdapter {
        viewModel.feedType = tagList?.title ?: "all"
        return object : FeedAdapter(this@TagFeedListActivity, KEY_FEED_TYPE) {
            override fun onViewAttachedToWindow(holder: ViewHolder) {
                super.onViewAttachedToWindow(holder)
                if (holder.isVideoItem) {
                    playDetector?.addTarget(holder.listPlayerView)
                }
            }

            override fun onViewDetachedFromWindow(holder: ViewHolder) {
                super.onViewDetachedFromWindow(holder)
                if (holder.isVideoItem) {
                    playDetector?.removeTarget(holder.listPlayerView)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        playDetector?.onResume()
    }

    override fun onPause() {
        super.onPause()
        if (shouldPause) {
            playDetector?.onPause()
        } else {
            playDetector?.onResume()
        }
    }

    override fun onDestroy() {
        playDetector = null
        PageListPlayManager.release(KEY_FEED_TYPE)
        super.onDestroy()
    }

    companion object {
        const val KEY_FEED_TYPE = "key_feed_type"
        const val KEY_TAG_LIST = "key_tag_list"
        fun startActivity(context: Context, tagList: TagList) {
            context.startActivity(Intent(context, TagFeedListActivity::class.java).apply {
                putExtra(KEY_TAG_LIST, tagList)
            })
        }
    }
}