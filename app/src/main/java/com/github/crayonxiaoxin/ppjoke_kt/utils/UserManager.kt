package com.github.crayonxiaoxin.ppjoke_kt.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.github.crayonxiaoxin.lib_common.global.AppGlobals
import com.github.crayonxiaoxin.ppjoke_kt.model.User
import com.google.gson.Gson
import kotlinx.coroutines.flow.first


object UserManager {

    private val KEY_DATA = stringPreferencesKey("data")
    private val Context.userStore by preferencesDataStore("user_preferences")
    private val userStore = AppGlobals.application.userStore

    suspend fun get(): User {
        val data = userStore.data.first()
        return Gson().fromJson(data[KEY_DATA], User::class.java)
    }

    suspend fun set(user: User) {
        val toJson = Gson().toJson(user)
        userStore.edit {
            it[KEY_DATA] = toJson
        }
    }


}