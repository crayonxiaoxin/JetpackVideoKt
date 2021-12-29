package com.github.crayonxiaoxin.ppjoke_kt.ui.my

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.github.crayonxiaoxin.lib_common.utils.StatusBar
import com.github.crayonxiaoxin.ppjoke_kt.R
import com.github.crayonxiaoxin.ppjoke_kt.databinding.ActivityProfileBinding
import com.github.crayonxiaoxin.ppjoke_kt.utils.UserManager
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import kotlin.math.abs

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    companion object {
        const val KEY_TAB_TYPE = "tab_type"

        const val TAB_TYPE_ALL = "tab_all"
        const val TAB_TYPE_FEED = "tab_feed"
        const val TAB_TYPE_COMMENT = "tab_comment"

        fun intentStartActivity(context: Context, tabType: String): Intent {
            return Intent(context, ProfileActivity::class.java).apply {
                putExtra(KEY_TAB_TYPE, tabType)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        StatusBar.fitSystemBar(this, darkIcons = true)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        viewPager = binding.viewPager
        tabLayout = binding.tabLayout

        lifecycleScope.launch {
            UserManager.get()?.let { binding.user = it }
        }

        binding.actionBack.setOnClickListener { finish() }

        val tabs = resources.getStringArray(R.array.profile_tabs)
        viewPager.adapter = object : FragmentStateAdapter(supportFragmentManager, lifecycle) {
            override fun getItemCount(): Int {
                return tabs.size
            }

            private fun getTabType(position: Int): String {
                return when (position) {
                    0 -> TAB_TYPE_ALL
                    1 -> TAB_TYPE_FEED
                    2 -> TAB_TYPE_COMMENT
                    else -> TAB_TYPE_ALL
                }
            }

            override fun createFragment(position: Int): Fragment {
                return ProfileListFragment.newInstance(getTabType(position))
            }

        }
        val initTabPosition = getInitTabPosition()
        if (initTabPosition != 0) {
            viewPager.post { viewPager.setCurrentItem(initTabPosition, false) }
        }
        TabLayoutMediator(tabLayout, viewPager, false) { tab, position ->
            tab.text = tabs[position]
        }.attach()
        binding.appBar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val expand = abs(verticalOffset) < appBarLayout.totalScrollRange
            binding.expand = expand
        })
    }

    private fun getInitTabPosition(): Int {
        val initTab = intent.getStringExtra(KEY_TAB_TYPE) ?: return 0
        return when (initTab) {
            TAB_TYPE_ALL -> 0
            TAB_TYPE_FEED -> 1
            TAB_TYPE_COMMENT -> 2
            else -> 0
        }
    }
}