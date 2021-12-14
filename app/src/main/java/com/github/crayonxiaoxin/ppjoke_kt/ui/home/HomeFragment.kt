package com.github.crayonxiaoxin.ppjoke_kt.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.github.crayonxiaoxin.lib_common.global.toast
import com.github.crayonxiaoxin.lib_nav_annotation.FragmentDestination
import com.github.crayonxiaoxin.ppjoke_kt.R
import com.github.crayonxiaoxin.ppjoke_kt.model.prepare
import com.github.crayonxiaoxin.ppjoke_kt.utils.ApiService
import com.github.crayonxiaoxin.ppjoke_kt.utils.apiService
import kotlinx.coroutines.launch

@FragmentDestination("main/tabs/home", asStarter = true)
class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        lifecycleScope.launch {
            val res = prepare { apiService.queryHotFeedsList(0, "") }
            toast("res = ${res.isSuccess}")
            res.getOrNull()?.forEach {
                Log.e("TAG", "onCreateView: $it")
            }
        }
        return view
    }
}