package com.github.crayonxiaoxin.lib_network

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 统一 try catch
 */
suspend fun <T> result(request: suspend CoroutineScope.() -> T): Result<T> {
    return withContext(Dispatchers.IO) {
        try {
            Result.success(request())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}