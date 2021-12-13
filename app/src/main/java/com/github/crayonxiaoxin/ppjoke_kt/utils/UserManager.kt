package com.github.crayonxiaoxin.ppjoke_kt.utils

import android.content.Context
import android.content.Intent
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.github.crayonxiaoxin.lib_common.global.AppGlobals
import com.github.crayonxiaoxin.ppjoke_kt.model.User
import com.github.crayonxiaoxin.ppjoke_kt.ui.login.LoginActivity
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


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

    fun login(context: Context): StateFlow<User?> {
        context.startActivity(Intent(context, LoginActivity::class.java).also {
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
        return flow
    }

    fun isLogin(): Boolean {
        return runBlocking {
            isLoggedIn()
        }
    }

    fun logout() {
        runBlocking {
            set(null)
        }
    }


}