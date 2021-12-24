package com.github.crayonxiaoxin.ppjoke_kt.ui.publish

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.github.crayonxiaoxin.lib_common.utils.StatusBar
import com.github.crayonxiaoxin.lib_nav_annotation.ActivityDestination
import com.github.crayonxiaoxin.ppjoke_kt.R
import com.github.crayonxiaoxin.ppjoke_kt.databinding.ActivityPublishBinding

@ActivityDestination("main/tabs/publish", true)
class PublishActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityPublishBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        StatusBar.fitSystemBar(this, darkIcons = true)
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_publish)
        binding.actionClose.setOnClickListener(this)
        binding.actionPublish.setOnClickListener(this)
        binding.actionAddTag.setOnClickListener(this)
        binding.actionAddFile.setOnClickListener(this)
        binding.actionDeleteFile.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val id = v?.id ?: return
        when (id) {
            R.id.action_close -> showExitDialog()
            R.id.action_publish -> publish()
            R.id.action_add_tag -> {

            }
            R.id.action_add_file -> {

            }
            R.id.action_delete_file -> {

            }
        }
    }

    private fun publish() {

    }

    private fun showExitDialog() {
        AlertDialog.Builder(this)
            .setMessage(R.string.publish_exit_message)
            .setNegativeButton(R.string.publish_exit_action_cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(R.string.publish_exit_action_ok) { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .create().show()
    }
}