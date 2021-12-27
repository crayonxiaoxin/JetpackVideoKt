package com.github.crayonxiaoxin.ppjoke_kt.ui.publish

import android.annotation.SuppressLint
import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.work.*
import com.github.crayonxiaoxin.lib_common.dialog.LoadingDialog
import com.github.crayonxiaoxin.lib_common.global.toast
import com.github.crayonxiaoxin.lib_common.utils.StatusBar
import com.github.crayonxiaoxin.lib_nav_annotation.ActivityDestination
import com.github.crayonxiaoxin.ppjoke_kt.R
import com.github.crayonxiaoxin.ppjoke_kt.base.prepare
import com.github.crayonxiaoxin.ppjoke_kt.databinding.ActivityPublishBinding
import com.github.crayonxiaoxin.ppjoke_kt.model.Feed
import com.github.crayonxiaoxin.ppjoke_kt.model.TagList
import com.github.crayonxiaoxin.ppjoke_kt.utils.FileUtils
import com.github.crayonxiaoxin.ppjoke_kt.utils.apiService
import com.github.crayonxiaoxin.ppjoke_kt.utils.setImageUri
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

@ActivityDestination("main/tabs/publish", true)
class PublishActivity : AppCompatActivity(), View.OnClickListener {
    private var coverUploadUrl: String? = null
    private var fileUploadUrl: String? = null
    private var fileUUID: UUID? = null
    private var coverUUID: UUID? = null
    private var mCoverPath: String = ""
    private var width: Int = 0
    private var height: Int = 0
    private var isVideo: Boolean = false
    private var fileUri: Uri? = null
    private var mTagList: TagList? = null
    private lateinit var binding: ActivityPublishBinding
    private var mLoadingDialog: LoadingDialog? = null
    private val addFileResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                it.data?.let { data ->
                    fileUri = data.getParcelableExtra<Uri>(CaptureActivity.RESULT_FILE_PATH)
                    width = data.getIntExtra(CaptureActivity.RESULT_FILE_WIDTH, 0)
                    height = data.getIntExtra(CaptureActivity.RESULT_FILE_HEIGHT, 0)
                    isVideo = data.getBooleanExtra(CaptureActivity.RESULT_FILE_TYPE, false)
                    showFileThumbnail()
                }
            }
        }

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
            R.id.action_add_tag -> addTag()
            R.id.action_add_file -> addFile()
            R.id.action_delete_file -> resetFile()
        }
    }

    private fun addTag() {
        val fragment = TabBottomSheetFragment()
        fragment.setOnTagItemSelected {
            mTagList = it
            binding.actionAddTag.text = it.title
        }
        fragment.show(supportFragmentManager, "tag-dialog")
    }

    private fun addFile() {
        addFileResult.launch(CaptureActivity.intentStartActivity(this))
    }

    private fun publish() {
        showLoading()
        val workRequestList: MutableList<OneTimeWorkRequest> = ArrayList()
        if (fileUri != null) {
            val fileRequest = getOneTimeWork(fileUri!!.toString(), true)
            fileUUID = fileRequest.id
            workRequestList.add(fileRequest)
            if (isVideo) {
                FileUtils.generateVideoCover(this, fileUri!!).observe(this) {
                    it?.let {
                        mCoverPath = it
                        val coverRequest = getOneTimeWork(mCoverPath, false)
                        coverUUID = coverRequest.id
                        workRequestList.add(coverRequest)
                        enqueue(workRequestList)
                    }
                }
            } else {
                enqueue(workRequestList)
            }
        } else {
            publishFeed()
        }
    }

    private fun enqueue(workRequestList: MutableList<OneTimeWorkRequest>) {
        val workContinuation = WorkManager.getInstance(this).beginWith(workRequestList)
        workContinuation.enqueue()
        workContinuation.workInfosLiveData.observe(this) {
            var completedCount = 0
            it.forEach { workInfo ->
                val state = workInfo.state
                val outputData = workInfo.outputData
                val uuid = workInfo.id
                if (state == WorkInfo.State.FAILED) {
                    if (uuid == coverUUID) {
                        toast(getString(R.string.file_upload_cover_message))
                    } else if (uuid == fileUUID) {
                        toast(getString(R.string.file_upload_original_message))
                    }
                } else if (state == WorkInfo.State.SUCCEEDED) {
                    val fileUrl = outputData.getString(UploadWorker.OUTPUT_KEY_FILE_URL)
                    if (uuid == coverUUID) {
                        coverUploadUrl = fileUrl
                    } else if (uuid == fileUUID) {
                        fileUploadUrl = fileUrl
                    }
                    completedCount += 1
                }
            }
            if (completedCount >= it.size) {
                publishFeed()
            }
        }
    }

    private fun publishFeed() {
        lifecycleScope.launch {
            val res = prepare {
                apiService.publishFeed(
                    feedText = binding.inputView.text.toString(),
                    feedType = if (isVideo) Feed.TYPE_VIDEO else Feed.TYPE_IMAGE,
                    tagId = mTagList?.tagId,
                    tagTitle = mTagList?.title,
                    coverUrl = if (isVideo) coverUploadUrl else null,
                    fileUrl = fileUploadUrl,
                    fileWidth = width,
                    fileHeight = height
                )
            }
            hideLoading()
            if (res.isSuccess && res.getOrNull() != null) {
                toast(getString(R.string.feed_publisj_success))
                this@PublishActivity.finish()
            } else {
                toast(res.exceptionOrNull()?.message ?: "发布失败")
            }
        }
    }

    private fun getOneTimeWork(filePath: String, isUri: Boolean = false): OneTimeWorkRequest {
        val inputData = Data.Builder()
            .putString(UploadWorker.INPUT_KEY_FILE, filePath)
            .putBoolean(UploadWorker.INPUT_KEY_IS_URI, isUri)
            .build()
        return OneTimeWorkRequestBuilder<UploadWorker>()
            .setInputData(inputData)
            .build()
    }

    private fun showLoading() {
        if (mLoadingDialog == null) {
            mLoadingDialog = LoadingDialog(this).apply {
                setLoadingText(getString(R.string.feed_publish_ing))
            }
        }
        mLoadingDialog?.show()
    }

    private fun hideLoading() {
        lifecycleScope.launch {
            mLoadingDialog?.hide()
        }
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

    private fun showFileThumbnail() {
        if (fileUri == null) return
        binding.actionAddFile.visibility = View.GONE
        binding.fileContainer.visibility = View.VISIBLE
        binding.cover.setImageUri(fileUri!!)
        binding.playBtn.visibility = if (isVideo) View.VISIBLE else View.GONE
        binding.cover.setOnClickListener {
            startActivity(PreviewActivity.intentStartActivity(this, fileUri!!, isVideo))
        }
    }

    private fun resetFile() {
        binding.actionAddFile.visibility = View.VISIBLE
        binding.fileContainer.visibility = View.GONE
        binding.cover.setImageDrawable(null)
        fileUri = null
        width = 0
        height = 0
        isVideo = false
    }
}