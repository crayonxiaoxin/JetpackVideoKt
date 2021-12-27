package com.github.crayonxiaoxin.ppjoke_kt.ui.publish

import android.content.Context
import android.net.Uri
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.github.crayonxiaoxin.lib_common.utils.FileUploadManager

class UploadWorker : Worker {
    companion object{
        const val INPUT_KEY_FILE = "file"
        const val INPUT_KEY_IS_URI = "isUri"
        const val OUTPUT_KEY_FILE_URL = "fileUrl"
    }
    constructor(context: Context, workerParams: WorkerParameters) : super(context, workerParams)

    override fun doWork(): Result {
        val file = inputData.getString(INPUT_KEY_FILE)
        val isUri = inputData.getBoolean(INPUT_KEY_IS_URI, false)
        val fileUrl = if (isUri) {
            FileUploadManager.upload(Uri.parse(file))
        } else {
            FileUploadManager.upload(file)
        }
        return if (fileUrl.isNullOrEmpty()) {
            Result.failure()
        } else {
            val outputData = Data.Builder().putString(OUTPUT_KEY_FILE_URL, fileUrl).build()
            Result.success(outputData)
        }
    }
}