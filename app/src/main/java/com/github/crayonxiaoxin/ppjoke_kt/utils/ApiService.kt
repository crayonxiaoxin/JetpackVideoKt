package com.github.crayonxiaoxin.ppjoke_kt.utils

import com.github.crayonxiaoxin.lib_network.NetworkManager
import com.github.crayonxiaoxin.ppjoke_kt.BuildConfig
import com.github.crayonxiaoxin.ppjoke_kt.base.Base
import com.github.crayonxiaoxin.ppjoke_kt.model.*
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("user/insert")
    suspend fun insertUser(
        @Query("avatar") avatar: String,
        @Query("expires_time") expires_time: String,
        @Query("name") name: String,
        @Query("qqOpenId") qqOpenId: String,
    ): Base<User>

    @GET("feeds/queryHotFeedsList")
    suspend fun queryHotFeedsList(
        @Query("feedId") feedId: Int,
        @Query("feedType") feedType: String,
        @Query("userId") userId: String = UserManager.userId(),
        @Query("pageCount") pageCount: Int = 10
    ): Base<List<Feed>>

    @GET("ugc/toggleFeedLike")
    suspend fun toggleFeedLike(
        @Query("itemId") itemId: Long,
        @Query("userId") userId: String = UserManager.userId()
    ): Base<HasLiked>

    @GET("ugc/dissFeed")
    suspend fun dissFeed(
        @Query("itemId") itemId: Long,
        @Query("userId") userId: String = UserManager.userId()
    ): Base<HasLiked>

    @GET("ugc/increaseShareCount")
    suspend fun increaseShareCount(
        @Query("itemId") itemId: Long
    ): Base<ShareCount>

    @GET("ugc/toggleFavorite")
    suspend fun toggleFavorite(
        @Query("itemId") itemId: Long,
        @Query("userId") userId: String = UserManager.userId()
    )

    @GET("ugc/toggleUserFollow")
    suspend fun toggleUserFollow(
        @Query("userId") userId: Int,
        @Query("followUserId") followUserId: String = UserManager.userId()
    )

    @GET("ugc/toggleCommentLike")
    suspend fun toggleCommentLike(
        @Query("commentId") commentId: Long,
        @Query("userId") userId: String = UserManager.userId()
    ): Base<HasLiked>

    @GET("comment/deleteComment")
    suspend fun deleteComment(
        @Query("itemId") itemId: Long,
        @Query("commentId") commentId: Long,
        @Query("userId") userId: String = UserManager.userId()
    )

    @GET("tag/toggleTagFollow")
    suspend fun toggleTagFollow(
        @Query("tagId") tagId: Long,
        @Query("userId") userId: String = UserManager.userId()
    ): Base<HasFollow>

    @GET("tag/queryTagList")
    suspend fun queryTagList(
        @Query("tagId") tagId: Long,
        @Query("tagType") tagType: String,
        @Query("offset") offset: Int = 0,
        @Query("pageCount") pageCount: Int = 10,
        @Query("userId") userId: String = UserManager.userId(),
    ): Base<List<TagList>>

    @GET("user/query")
    suspend fun queryUser(
        @Query("userId") userId: String = UserManager.userId()
    ): Base<User>

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

