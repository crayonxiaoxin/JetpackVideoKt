package com.github.crayonxiaoxin.ppjoke_kt.utils

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.github.crayonxiaoxin.lib_common.global.AppGlobals
import com.github.crayonxiaoxin.ppjoke_kt.model.User
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first


object UserManager {

    private val KEY_DATA = stringPreferencesKey("data")
    private val Context.userStore by preferencesDataStore("user_preferences")
    private val userStore = AppGlobals.application.userStore
    private val _flow = MutableStateFlow<User?>(null)
    val flow: StateFlow<User?> = _flow

    suspend fun get(): User? {
        val data = userStore.data.first()
        return try {
            Gson().fromJson(data[KEY_DATA], User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun set(user: User?) {
        val toJson = if (user == null) "" else Gson().toJson(user)
        userStore.edit {
            it[KEY_DATA] = toJson
        }
        _flow.value = user
    }

    suspend fun isLoggedIn(): Boolean {
        val userId = get()?.userId ?: 0
        return userId != 0
    }

    suspend fun logout() {
        set(null)
    }


}