package com.github.crayonxiaoxin.ppjoke_kt

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.github.crayonxiaoxin.lib_common.utils.StatusBar
import com.github.crayonxiaoxin.ppjoke_kt.databinding.ActivityMainBinding
import com.github.crayonxiaoxin.ppjoke_kt.utils.AppConfig
import com.github.crayonxiaoxin.ppjoke_kt.utils.NavGraphBuilder
import com.github.crayonxiaoxin.ppjoke_kt.utils.UserManager
import com.google.android.material.navigation.NavigationBarView
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener {

    private lateinit var _binding: ActivityMainBinding
    private val binding get() = _binding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        // 设回本身的主题，否则 splash 会一直保留
        setTheme(R.style.Theme_Ppjoke_kt)
        StatusBar.fitSystemBar(this, darkIcons = true)
        super.onCreate(savedInstanceState)

//        UserManager.logout()

        _binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()
        NavigationUI.setupWithNavController(binding.navView, navController)

        NavGraphBuilder.build(navController, this, binding.navHostFragment.id)

        binding.navView.setOnItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val destConfig = AppConfig.getDestConfig()
        val iterator = destConfig.entries.iterator()
        while (iterator.hasNext()) {
            val next = iterator.next()
            val value = next.value
            if (!UserManager.isLogin() && value.needLogin && value.id == item.itemId) {
                lifecycleScope.launch {
                    UserManager.login(this@MainActivity)
                }
                return false
            }
        }
        navController.navigate(item.itemId)
        return item.title.isNotEmpty()
    }

    override fun onBackPressed() {
        val startId = navController.graph.startDestination
        val currentId = navController.currentDestination?.id
        if (currentId != startId) {
            binding.navView.selectedItemId = startId
        } else {
            finish()
        }
    }
}