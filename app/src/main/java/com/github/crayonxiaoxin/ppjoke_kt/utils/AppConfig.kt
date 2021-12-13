package com.github.crayonxiaoxin.ppjoke_kt.utils

import com.github.crayonxiaoxin.lib_common.global.AppGlobals
import com.github.crayonxiaoxin.ppjoke_kt.model.BottomBar
import com.github.crayonxiaoxin.ppjoke_kt.model.Destination
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

object AppConfig {
    private var sDestConfig: HashMap<String, Destination>? = null
    private var sBottomBar: BottomBar? = null

    fun getDestConfig(): HashMap<String, Destination> {
        val parseFile = parseFile("destination.json")
        if (sDestConfig == null) {
            sDestConfig = Gson().fromJson<HashMap<String, Destination>>(
                parseFile,
                object : TypeToken<HashMap<String, Destination>>() {}.type
            )
        }
        return sDestConfig!!
    }

    fun getBottomBarConfig(): BottomBar {
        val parseFile = parseFile("main_tabs_config.json")
        if (sBottomBar == null) {
            sBottomBar = Gson().fromJson<BottomBar>(
                parseFile,
                BottomBar::class.java
            )
        }
        return sBottomBar!!
    }

    private fun parseFile(filename: String): String {
        val assets = AppGlobals.application.resources.assets
        var inputStream: InputStream? = null
        var bufferedReader: BufferedReader? = null
        val stringBuilder = StringBuilder()
        try {
            inputStream = assets.open(filename)
            bufferedReader = BufferedReader(InputStreamReader(inputStream))
            var line = bufferedReader.readLine()
            while (line != null) {
                stringBuilder.append(line)
                line = bufferedReader.readLine()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                inputStream?.close()
                bufferedReader?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return stringBuilder.toString()
    }
}