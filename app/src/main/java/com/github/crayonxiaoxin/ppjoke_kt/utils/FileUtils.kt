package com.github.crayonxiaoxin.ppjoke_kt.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.MediaMetadataRetriever
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.util.Log
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

object FileUtils {
    fun generateVideoCover(context: Context, filePath: Uri): LiveData<String?> {
        val liveData: MutableLiveData<String?> = MutableLiveData()
        CoroutineScope(Dispatchers.IO).launch {
            // 截取某一帧
            val retriever = MediaMetadataRetriever()
            retriever.setDataSource(context, filePath)
            val bitmap = retriever.frameAtTime

            if (bitmap == null) {
                liveData.postValue(null)
            } else {
                var fileOutputStream: FileOutputStream? = null
                val byteArray = compressBitmap(bitmap, 200)
                val file = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "${System.currentTimeMillis()}.jpeg"
                )
                try {
                    file.createNewFile()
                    fileOutputStream = FileOutputStream(file)
                    fileOutputStream.write(byteArray)
                    MediaScannerConnection.scanFile(
                        context,
                        arrayOf(file.parent),
                        arrayOf("image/jpeg"),
                        null
                    )
                    liveData.postValue(file.absolutePath)
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    fileOutputStream?.let {
                        it.flush()
                        it.close()
                        fileOutputStream = null
                    }
                }
            }
        }
        return liveData
    }

    private fun compressBitmap(bitmap: Bitmap, limit: Int): ByteArray? {
        if (limit > 0) {
            val baos = ByteArrayOutputStream()
            var option = 100
            bitmap.compress(Bitmap.CompressFormat.JPEG, option, baos)
            while (baos.toByteArray().size > limit * 1024) {
                Log.e("TAG", "compressBitmap: option = $option ${baos.size()}")
                if (option == 0) break
                option -= 5
                baos.reset() // 一定要重置  否则 baos 会越来越大
                bitmap.compress(Bitmap.CompressFormat.JPEG, option, baos)
            }
            val toByteArray = baos.toByteArray()
            try {
                baos.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return toByteArray
        } else {
            return null
        }
    }

    fun getPhotoRotation(uri: Uri?): Int {
        return if (uri == null) {
            0
        } else {
            val inputStream = uri.toFile().inputStream()
            val orientation = ExifInterface(inputStream).getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
        }
    }

    fun rotatePhoto(uri: Uri?, degrees: Float): Uri? {
        if (uri == null) return null
        val toFile = uri.toFile()
        val bitmap = BitmapFactory.decodeStream(toFile.inputStream())
        val width = bitmap.width
        val height = bitmap.height
        val matrix = Matrix()
        matrix.postRotate(degrees)
        Log.e("TAG", "rotatePhoto: rotation = $degrees" )
        val createBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            "${System.currentTimeMillis()}-1.jpeg"
        )
        try {
            file.createNewFile()
            val bufferedOutputStream = BufferedOutputStream(FileOutputStream(file))
            createBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bufferedOutputStream)
            bufferedOutputStream.flush()
            bufferedOutputStream.close()
            return file.toUri()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}