package com.github.crayonxiaoxin.lib_common.global

import android.annotation.SuppressLint
import android.app.Application
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object AppGlobals {
    private var sApplication: Application? = null
    val application: Application get() = application()!!

    @SuppressLint("DiscouragedPrivateApi", "PrivateApi")
    fun application(): Application? {
        if (sApplication == null) {
            try {
                val method = Class.forName("android.app.ActivityThread")
                    .getDeclaredMethod("currentApplication")
                sApplication = method.invoke(null, *arrayOf()) as Application
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return sApplication
    }

}

fun toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    AppGlobals.application()?.let {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(it, message, duration).show()
        }
    }
}