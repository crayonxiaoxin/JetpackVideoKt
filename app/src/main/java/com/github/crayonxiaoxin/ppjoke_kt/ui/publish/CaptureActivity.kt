package com.github.crayonxiaoxin.ppjoke_kt.ui.publish

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.util.Size
import android.view.OrientationEventListener
import android.view.Surface
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.github.crayonxiaoxin.lib_common.global.toast
import com.github.crayonxiaoxin.ppjoke_kt.R
import com.github.crayonxiaoxin.ppjoke_kt.databinding.ActivityCaptureBinding
import com.github.crayonxiaoxin.ppjoke_kt.ui.view.RecordView
import java.io.File

class CaptureActivity : AppCompatActivity() {

    companion object {
        const val RESULT_FILE_PATH = "file_path"
        const val RESULT_FILE_TYPE = "file_type"
        const val RESULT_FILE_WIDTH = "file_width"
        const val RESULT_FILE_HEIGHT = "file_height"
        fun intentStartActivity(context: Context): Intent {
            return Intent(context, CaptureActivity::class.java)
        }
    }

    private lateinit var binding: ActivityCaptureBinding

    private var imageCapture: ImageCapture? = null
    private var videoCapture: VideoCapture? = null
    private var takingPicture = false
    private val resolution = Size(1280, 720)
    private var outputFileUri: Uri? = null
    private val PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO
    )
    private var deniedPermissions: MutableList<String> = ArrayList()
    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            deniedPermissions.clear()
            it.entries.filter { !it.value }.forEach {
                deniedPermissions.add(it.key)
            }
            if (deniedPermissions.isEmpty()) {
                bindCameraX()
            } else {
                AlertDialog.Builder(this)
                    .setMessage(R.string.capture_permission_message)
                    .setNegativeButton(R.string.capture_permission_no) { dialog, _ ->
                        dialog.dismiss()
                        finish()
                    }
                    .setPositiveButton(R.string.capture_permission_ok) { dialog, _ ->
                        dialog.dismiss()
                        retryRequestPermissions()
                    }
                    .create().show()
            }
        }
    private val previewResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val apply = Intent().apply {
                    putExtra(RESULT_FILE_PATH, outputFileUri)
                    putExtra(RESULT_FILE_TYPE, !takingPicture)
                    // 宽高互换是因为 resolution 是指横屏的 size
                    putExtra(RESULT_FILE_WIDTH, resolution.height)
                    putExtra(RESULT_FILE_HEIGHT, resolution.width)
                }
                setResult(RESULT_OK, apply)
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_capture)

        requestPermissions.launch(PERMISSIONS)

        binding.recordView.setOnRecordListener(object : RecordView.RecordListener {
            override fun onClick() {
                takingPicture = true
                val file = File(
//                    getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "${System.currentTimeMillis()}.jpeg"
                )
                val outputFileOptions = ImageCapture.OutputFileOptions.Builder(file).build()
                imageCapture?.takePicture(
                    outputFileOptions,
                    ContextCompat.getMainExecutor(this@CaptureActivity),
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
//                            val degrees =
//                                FileUtils.getPhotoRotation(outputFileResults.savedUri).toFloat()
//                            val rotateUri =
//                                FileUtils.rotatePhoto(outputFileResults.savedUri, -180f)
//                            Log.e("TAG", "onImageSaved: $degrees $rotateUri")
                            onFileSave(outputFileResults.savedUri)
                        }

                        override fun onError(exception: ImageCaptureException) {
                            toast("文件保存失败，原因：${exception.message}")
                        }
                    })
            }

            @SuppressLint("RestrictedApi", "MissingPermission")
            override fun onLongClick() {
                takingPicture = false
                val file = File(
//                    getExternalFilesDir(Environment.DIRECTORY_MOVIES), // 不可被扫描
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "${System.currentTimeMillis()}.mp4"
                )
                val outputFileOptions = VideoCapture.OutputFileOptions.Builder(file).build()
                videoCapture?.startRecording(outputFileOptions,
                    ContextCompat.getMainExecutor(this@CaptureActivity),
                    object : VideoCapture.OnVideoSavedCallback {
                        override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {
                            onFileSave(outputFileResults.savedUri)
                        }

                        override fun onError(
                            videoCaptureError: Int,
                            message: String,
                            cause: Throwable?
                        ) {
                            toast("文件保存失败，原因：${message}")
                        }

                    })
            }

            @SuppressLint("RestrictedApi")
            override fun onFinished() {
                videoCapture?.stopRecording()
            }

        })
    }

    private fun onFileSave(savedUri: Uri?) {
        savedUri?.let { uri ->
            outputFileUri = uri
            val mimeType = if (takingPicture) "image/jpeg" else "video/mp4"
            MediaScannerConnection.scanFile(this, arrayOf(uri.path), arrayOf(mimeType), null)

            Log.e("TAG", "onFileSave: ")
            previewResult.launch(
                PreviewActivity.intentStartActivity(
                    this,
                    uri,
                    !takingPicture,
                    "完成"
                )
            )
        }
    }

    private fun retryRequestPermissions() {
        requestPermissions.launch(deniedPermissions.toTypedArray())
    }

    @SuppressLint("RestrictedApi")
    private fun bindCameraX() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            var cameraProvider: ProcessCameraProvider? = null
            try {
                cameraProvider = cameraProviderFuture.get()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (cameraProvider == null) return@addListener
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()
            val preview = Preview.Builder()
                .setTargetResolution(resolution)
                .setCameraSelector(cameraSelector)
                .build()
            preview.setSurfaceProvider(binding.textureView.surfaceProvider)
            imageCapture = ImageCapture.Builder()
//                .setTargetResolution(resolution) // 这个不知道为何，没办法获取正确的 rotation
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .setCameraSelector(cameraSelector)
                .build()
            videoCapture = VideoCapture.Builder()
//                .setTargetResolution(resolution) // 这个也可以获取正确的 rotation
                .setTargetAspectRatio(AspectRatio.RATIO_16_9)
                .setCameraSelector(cameraSelector)
                .setVideoFrameRate(25)
                .setAudioBitRate(3 * 1024 * 1024)
                .build()
            val orientationListener = object : OrientationEventListener(this) {
                override fun onOrientationChanged(orientation: Int) {
                    val rotation = when (orientation) {
                        in 45..134 -> Surface.ROTATION_270
                        in 135..224 -> Surface.ROTATION_180
                        in 225..314 -> Surface.ROTATION_90
                        else -> Surface.ROTATION_0
                    }
                    imageCapture?.targetRotation = rotation
                    videoCapture?.setTargetRotation(rotation)
                }
            }
            orientationListener.enable()
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                preview,
                imageCapture,
                videoCapture
            )
        }, ContextCompat.getMainExecutor(this))

    }

}