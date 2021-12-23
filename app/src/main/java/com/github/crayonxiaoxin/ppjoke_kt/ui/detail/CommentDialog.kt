package com.github.crayonxiaoxin.ppjoke_kt.ui.detail

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.lifecycle.lifecycleScope
import com.github.crayonxiaoxin.lib_common.global.toast
import com.github.crayonxiaoxin.lib_common.utils.FileUploadManager
import com.github.crayonxiaoxin.lib_common.utils.hideSoftInput
import com.github.crayonxiaoxin.lib_common.utils.showSoftInput
import com.github.crayonxiaoxin.ppjoke_kt.R
import com.github.crayonxiaoxin.ppjoke_kt.base.prepare
import com.github.crayonxiaoxin.ppjoke_kt.databinding.LayoutCommentDialogBinding
import com.github.crayonxiaoxin.ppjoke_kt.model.Comment
import com.github.crayonxiaoxin.ppjoke_kt.ui.publish.CaptureActivity
import com.github.crayonxiaoxin.ppjoke_kt.utils.FileUtils
import com.github.crayonxiaoxin.ppjoke_kt.utils.apiService
import com.github.crayonxiaoxin.ppjoke_kt.utils.setImageUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicInteger

class CommentDialog : AppCompatDialogFragment(), View.OnClickListener {
    private var coverUrl: String? = ""
    private var fileUrl: String? = ""

    private var isVideo: Boolean = false
    private var width: Int = 0
    private var height: Int = 0
    private var fileUri: Uri? = null
    private var itemId: Long = 0L
    private lateinit var binding: LayoutCommentDialogBinding
    private var mListener: ((comment: Comment) -> Unit)? = null
    private val cameraResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            Log.e("TAG", "ok2: $it")
            this.onActivityResult(it.resultCode, it.data)
        }

    companion object {
        const val KEY_ITEM_ID = "key_item_id"
        fun newInstance(itemId: Long): CommentDialog {
            val args = Bundle()
            args.putLong(KEY_ITEM_ID, itemId)
            val fragment = CommentDialog()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onStart() {
        super.onStart()
        // 在这里设置 window 属性才能正常生效（原因未知）
        dialog?.window?.let { window ->
            // 似乎这个不起效果？
            window.setWindowAnimations(R.style.sendCommentDialog)
            // 透明背景
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            val attributes = window.attributes
            attributes.dimAmount = 0f
            attributes.gravity = Gravity.BOTTOM
            attributes.flags =
                WindowManager.LayoutParams.FLAG_DIM_BEHIND or WindowManager.LayoutParams.FLAG_FULLSCREEN
            attributes.width = WindowManager.LayoutParams.MATCH_PARENT
            attributes.height = WindowManager.LayoutParams.WRAP_CONTENT
            window.attributes = attributes

            // 取消软键盘弹起动画效果
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING)

            // 点击空白处 隐藏 dialog
            window.decorView.setOnTouchListener { v, event ->
                v.performClick()
                if (event.action == MotionEvent.ACTION_DOWN) {
                    binding.inputView.hideSoftInput()
                    dismiss()
                }
                return@setOnTouchListener false
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        itemId = arguments?.getLong(KEY_ITEM_ID) ?: 0L
        binding = LayoutCommentDialogBinding.inflate(inflater, container, false)

        binding.commentVideo.setOnClickListener(this)
        binding.commentSend.setOnClickListener(this)
        binding.commentDelete.setOnClickListener(this)

        binding.root.also {
            it.layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.BOTTOM
            }
            // 防止点击 frameLayout 隐藏 dialog
            it.isClickable = true
            it.isFocusable = true
            // 自动获取焦点 弹起软键盘
            it.post {
                showSoftKeyBoard()
            }
        }

        return binding.root
    }

    private fun showSoftKeyBoard() {
        binding.inputView.apply {
            isFocusable = true
            isFocusableInTouchMode = true
            requestFocus()
            showSoftInput()
        }
    }

    override fun onClick(v: View?) {
        val id = v?.id ?: 0
        when (id) {
            R.id.comment_send -> publishComment()
            R.id.comment_video -> {
                cameraResult.launch(CaptureActivity.intentStartActivity(requireContext()))
            }
            R.id.comment_delete -> resetFile()
        }
    }

    private fun resetFile() {
        fileUri = null
        width = 0
        height = 0
        isVideo = false
        binding.commentVideo.alpha = 1f
        binding.commentVideo.isEnabled = true
        binding.commentIconVideo.visibility = View.GONE
        binding.commentCover.setImageDrawable(null)
        binding.commentExtLayout.visibility = View.GONE
    }

    private fun publishComment() {
        if (fileUri != null) {
            if (isVideo) {
                try {
                    FileUtils.generateVideoCover(requireContext(), fileUri!!)
                        .observe(viewLifecycleOwner) {
                            it?.let {
                                uploadFile(it, fileUri)
                            }
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                uploadFile(null, fileUri)
            }
        } else {
            // 提交文字
            publish()
        }
    }

    private fun uploadFile(coverPath: String?, fileUri: Uri?) {
        lifecycleScope.launch {
            if (!coverPath.isNullOrEmpty()) {
                val uploadCover = async {
                    withContext(Dispatchers.IO) {
                        FileUploadManager.upload(coverPath)
                    }
                }
                try {
                    coverUrl = uploadCover.await()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            val uploadFile = async {
                withContext(Dispatchers.IO) {
                    FileUploadManager.upload(fileUri)
                }
            }
            try {
                fileUrl = uploadFile.await()
                if (!fileUrl.isNullOrEmpty()) {
                    Log.e("TAG", "uploadFile: $fileUrl $coverUrl")
                    publish()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun publish() {
        val commentText = binding.inputView.text.toString()
        lifecycleScope.launch {
            val videoUrl = if (isVideo) (fileUrl ?: "") else ""
            val imageUrl = if (isVideo) coverUrl else fileUrl
            val res = prepare {
                apiService.addComment(
                    itemId,
                    commentText,
                    width,
                    height,
                    videoUrl,
                    imageUrl ?: ""
                )
            }
            if (res.isSuccess) {
                res.getOrNull()?.let {
                    resetFile()
                    binding.inputView.setText("")
                    binding.inputView.hideSoftInput()
                    dismiss()
                    toast("评论成功")
                    mListener?.invoke(it)
                }
            } else {
                toast("评论失败: ${res.exceptionOrNull()?.message}")
            }
        }
    }

    private fun onActivityResult(resultCode: Int?, data: Intent?) {
        Log.e("TAG", "onActivityResult: data = $data")
        if (resultCode == Activity.RESULT_OK && data != null) {
            fileUri = data.getParcelableExtra<Uri>(CaptureActivity.RESULT_FILE_PATH)
            width = data.getIntExtra(CaptureActivity.RESULT_FILE_WIDTH, 0)
            height = data.getIntExtra(CaptureActivity.RESULT_FILE_HEIGHT, 0)
            isVideo = data.getBooleanExtra(CaptureActivity.RESULT_FILE_TYPE, false)
            Log.e("TAG", "onActivityResult: data = $data")
            if (fileUri != null) {
                binding.commentExtLayout.visibility = View.VISIBLE
                binding.commentCover.setImageUri(fileUri!!)
                if (isVideo) {
                    binding.commentIconVideo.visibility = View.VISIBLE
                }
            }
            binding.commentVideo.isEnabled = false
            binding.commentVideo.alpha = 0.4f
        }
    }

    fun setOnCommentAddedListener(listener: (comment: Comment) -> Unit) {
        this.mListener = listener
    }
}