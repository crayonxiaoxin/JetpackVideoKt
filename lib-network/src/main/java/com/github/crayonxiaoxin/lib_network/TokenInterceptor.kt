package com.github.crayonxiaoxin.lib_network

import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.internal.http2.ConnectionShutdownException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * 請求攔截器 - 添加 token
 */
abstract class TokenInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()
        val newRequestBuilder = request.newBuilder().apply {
            addHeader("Authorization", "Bearer " + getToken())
        }

        val buildRequest = newRequestBuilder.build()
        try {
            return chain.proceed(buildRequest)
        } catch (e: Exception) {
            val msg = when (e) {
                is SocketTimeoutException -> "Timeout - Please check your internet connection."
                is UnknownHostException -> "Unable to make a connection. Please check your internet connection."
                is ConnectionShutdownException -> "Connection shutdown. Please check your internet connection."
                is IOException -> "Server is unreachable, please try again later."
                is IllegalStateException -> "${e.message}"
                else -> "${e.message}"
            }
            return Response.Builder()
                .request(buildRequest)
                .protocol(Protocol.HTTP_1_1)
                .code(999)
                .message(msg)
                .build()
        }
    }

    abstract fun getToken(): String
}