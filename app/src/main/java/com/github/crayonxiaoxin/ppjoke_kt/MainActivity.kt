package com.github.crayonxiaoxin.ppjoke_kt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.github.crayonxiaoxin.lib_common.extension.FlowBus
import com.github.crayonxiaoxin.lib_common.global.toast
import com.github.crayonxiaoxin.lib_nav_annotation.ActivityDestination
import com.github.crayonxiaoxin.ppjoke_kt.model.User
import com.github.crayonxiaoxin.ppjoke_kt.utils.AppConfig
import com.github.crayonxiaoxin.ppjoke_kt.utils.UserManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@ActivityDestination("app/main/main")
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        lifecycleScope.launch {
//            repeatOnLifecycle(Lifecycle.State.CREATED) {
//                UserManager.flow.collect {
//                    toast("user = " + it?.userId)
//                }
//            }
            FlowBus.observe<String>(this@MainActivity, "test1") {
                toast("flowBus = $it")
                Log.e("TAG", "onCreate: $it")
            }
        }
        lifecycleScope.launch {
            FlowBus.post("test1", "world")
//            Log.e("TAG", "onCreate: " + UserManager.get())
//            val user = User(id = 1, userId = 1)
//            UserManager.set(user)
//            Log.e("TAG", "onCreate: " + UserManager.get())
        }
    }
}