package com.github.crayonxiaoxin.ppjoke_kt.utils

import com.github.crayonxiaoxin.lib_network.NetworkManager
import com.github.crayonxiaoxin.ppjoke_kt.BuildConfig
import com.github.crayonxiaoxin.ppjoke_kt.model.Base
import com.github.crayonxiaoxin.ppjoke_kt.model.Feed
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("feeds/queryHotFeedsList")
    suspend fun queryHotFeedsList(
        @Query("feedId") feedId: Int,
        @Query("feedType") feedType: String,
        @Query("userId") userId: String = UserManager.userId(),
        @Query("pageCount") pageCount: Int = 10
    ): Base<List<Feed>>

    companion object {
        operator fun invoke(): ApiService {
            return NetworkManager.create(
                "http://123.56.232.18:8080/serverdemo/",
                NetworkManager.defaultOkHttpClient(BuildConfig.DEBUG)
            )
        }
    }
}

val apiService: ApiService = ApiService()

