package com.github.crayonxiaoxin.ppjoke_kt.ui.publish

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.crayonxiaoxin.lib_nav_annotation.ActivityDestination
import com.github.crayonxiaoxin.ppjoke_kt.R

@ActivityDestination("main/tabs/publish", true)
class PublishActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_publish)
    }
}