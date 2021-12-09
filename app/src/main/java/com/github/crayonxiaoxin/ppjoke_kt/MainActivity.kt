package com.github.crayonxiaoxin.ppjoke_kt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.github.crayonxiaoxin.lib_common.global.toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        toast("haha")
    }
}