package com.github.crayonxiaoxin.lib_network

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkManager {

    fun defaultOkHttpClient(
        logging: Boolean = false,
        bearerToken: (() -> String) = { "" }
    ): OkHttpClient {
        return OkHttpClient.Builder().apply {
            val token = bearerToken()
            if (token.isNotEmpty()) {
                addInterceptor(object : TokenInterceptor() {
                    override fun getToken(): String = token
                })
            }
            if (logging) {
                addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
            }
        }.build()
    }

    inline fun <reified T> create(
        baseUrl: String,
        okHttpClient: OkHttpClient = defaultOkHttpClient()
    ): T {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(T::class.java)
    }
}