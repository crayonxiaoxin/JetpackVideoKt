package com.github.crayonxiaoxin.ppjoke_kt.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class Base<T>(
    var status: Int,
    var message: String,
    var data: Data<T>?,
) {
    data class Data<T>(var data: T? = null)
}

suspend fun <T, E : Base<T>> prepare(request: suspend CoroutineScope.() -> E): Result<T> {
    return withContext(Dispatchers.IO) {
        try {
            val res = request()
            if (res.data != null && res.data?.data != null) {
                Result.success(res.data!!.data!!)
            } else {
                Result.failure(Exception(res.message))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}