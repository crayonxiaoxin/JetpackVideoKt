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
open class SofaFragment : Fragment() {
    protected var tabLayoutMediator: TabLayoutMediator? = null
    protected lateinit var tabLayout: TabLayout
    protected lateinit var viewPager: ViewPager2
    protected lateinit var tabConfig: SofaTab
    protected var tabs: MutableList<SofaTab.Tab> = ArrayList()
    protected var fragmentMap: HashMap<Int, Fragment> = HashMap()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentSofaBinding.inflate(inflater, container, false)
        viewPager = binding.viewPager
        tabLayout = binding.tabLayout
        tabConfig = getTabs()

        tabs = ArrayList()
        val filter = tabConfig.tabs.filter { it.enable }
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
            viewPager.setCurrentItem(tabConfig.select, false)
        }

        return binding.root
    }

    protected open fun getTabs(): SofaTab {
        return AppConfig.getSofaTabConfig()
    }

    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            for (i in 0 until tabLayout.tabCount) {
                val tabAt = tabLayout.getTabAt(i)
                tabAt?.let {
                    val textView = it.customView as TextView
                    if (it.position == position) {
                        textView.textSize = tabConfig.activeSize.toFloat()
                        textView.setTypeface(Typeface.DEFAULT_BOLD)
                    } else {
                        textView.textSize = tabConfig.normalSize.toFloat()
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
            Color.parseColor(tabConfig.activeColor),
            Color.parseColor(tabConfig.normalColor)
        )
        textView.setTextColor(ColorStateList(state, colors))
        textView.text = tabs[position].title
        textView.textSize = tabConfig.normalSize.toFloat()
        return textView
    }

    private fun createTabFragment(position: Int): Fragment {
        var fragment = fragmentMap[position]
        if (fragment == null) {
            fragment = generateFragment(position)
            fragmentMap[position] = fragment
        }
        return fragment
    }

    protected open fun generateFragment(position: Int): Fragment {
        return HomeFragment.newInstance(tabs[position].tag)
    }


    override fun onDestroyView() {
        viewPager.unregisterOnPageChangeCallback(pageChangeCallback)
        tabLayoutMediator?.detach()
        super.onDestroyView()
    }
}