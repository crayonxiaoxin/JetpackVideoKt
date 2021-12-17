package com.github.crayonxiaoxin.ppjoke_kt.ui.sofa

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.github.crayonxiaoxin.lib_nav_annotation.FragmentDestination
import com.github.crayonxiaoxin.ppjoke_kt.R
import com.github.crayonxiaoxin.ppjoke_kt.databinding.FragmentSofaBinding
import com.github.crayonxiaoxin.ppjoke_kt.model.SofaTab
import com.github.crayonxiaoxin.ppjoke_kt.ui.home.HomeFragment
import com.github.crayonxiaoxin.ppjoke_kt.utils.AppConfig
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

@FragmentDestination("main/tabs/sofa")
class SofaFragment : Fragment() {
    private var tabLayoutMediator: TabLayoutMediator? = null
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var sofaTabConfig: SofaTab
    private var tabs: MutableList<SofaTab.Tab> = ArrayList()
    private var fragmentMap: HashMap<Int, Fragment> = HashMap()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSofaBinding.inflate(inflater, container, false)
        viewPager = binding.viewPager
        tabLayout = binding.tabLayout
        sofaTabConfig = AppConfig.getSofaTabConfig()

        tabs = ArrayList()
        val filter = sofaTabConfig.tabs.filter { it.enable }
        tabs.addAll(filter)

        viewPager.offscreenPageLimit = ViewPager2.OFFSCREEN_PAGE_LIMIT_DEFAULT
        viewPager.adapter = object : FragmentStateAdapter(childFragmentManager, lifecycle) {
            override fun getItemCount(): Int {
                return tabs.size
            }

            override fun createFragment(position: Int): Fragment {
                return createTabFragment(position)
            }
        }
        tabLayout.setSelectedTabIndicatorColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.color_theme
            )
        )
        tabLayoutMediator = TabLayoutMediator(tabLayout, viewPager, false) { tab, position ->
            tab.customView = makeTabView(position)
        }.also { it.attach() }
        viewPager.registerOnPageChangeCallback(pageChangeCallback)
        viewPager.post {
            viewPager.currentItem = sofaTabConfig.select
        }

        return binding.root
    }

    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            for (i in 0 until tabLayout.tabCount) {
                val tabAt = tabLayout.getTabAt(i)
                tabAt?.let {
                    val textView = it.customView as TextView
                    if (it.position == position) {
                        textView.textSize = sofaTabConfig.activeSize.toFloat()
                        textView.setTypeface(Typeface.DEFAULT_BOLD)
                    } else {
                        textView.textSize = sofaTabConfig.normalSize.toFloat()
                        textView.setTypeface(Typeface.DEFAULT)
                    }
                }
            }
        }
    }

    private fun makeTabView(position: Int): View {
        val textView = TextView(context)
        val state = arrayOf(
            intArrayOf(android.R.attr.state_selected),
            intArrayOf()
        )
        val colors = intArrayOf(
            Color.parseColor(sofaTabConfig.activeColor),
            Color.parseColor(sofaTabConfig.normalColor)
        )
        textView.setTextColor(ColorStateList(state, colors))
        textView.text = tabs[position].title
        textView.textSize = sofaTabConfig.normalSize.toFloat()
        return textView
    }

    private fun createTabFragment(position: Int): Fragment {
        var fragment = fragmentMap[position]
        if (fragment == null) {
            fragment = HomeFragment.newInstance(tabs[position].tag)
            fragmentMap[position] = fragment
        }
        return fragment
    }

    override fun onDestroyView() {
        viewPager.unregisterOnPageChangeCallback(pageChangeCallback)
        tabLayoutMediator?.detach()
        super.onDestroyView()
    }
}