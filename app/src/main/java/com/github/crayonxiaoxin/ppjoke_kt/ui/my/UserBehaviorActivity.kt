package com.github.crayonxiaoxin.ppjoke_kt.ui.my

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.github.crayonxiaoxin.lib_common.utils.StatusBar
import com.github.crayonxiaoxin.ppjoke_kt.R
import com.github.crayonxiaoxin.ppjoke_kt.databinding.ActivityLayoutListBinding

class UserBehaviorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLayoutListBinding
    private var mBehavior = BEHAVIOR_FAVORITE

    companion object {
        const val KEY_BEHAVIOR = "key_behavior"
        const val BEHAVIOR_FAVORITE = 0
        const val BEHAVIOR_HISTORY = 1

        fun intentStartActivity(context: Context, behavior: Int): Intent {
            return Intent(context, UserBehaviorActivity::class.java).apply {
                putExtra(KEY_BEHAVIOR, behavior)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        StatusBar.fitSystemBar(this, darkIcons = true)
        super.onCreate(savedInstanceState)
        mBehavior = intent.getIntExtra(KEY_BEHAVIOR, BEHAVIOR_FAVORITE)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_layout_list)
        binding.actionClose.setOnClickListener { finish() }
        binding.title.text = if (mBehavior == BEHAVIOR_FAVORITE) "收藏列表" else "历史记录"
        supportFragmentManager.beginTransaction()
            .add(R.id.frameLayout, UserBehaviorFragment.newInstance(mBehavior), "user-behavior")
            .commit()
    }
}