package com.github.crayonxiaoxin.ppjoke_kt.exoplayer

import android.net.Uri
import com.github.crayonxiaoxin.lib_common.global.AppGlobals
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.FileDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSink
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util

object PageListPlayManager {
    // 每个页面仅有一个播放器
    private val hashMap: HashMap<String, PageListPlay> = HashMap()
    private var mediaSourceFactory: ProgressiveMediaSource.Factory

    init {
        val application = AppGlobals.application
        val dataSourceFactory = DefaultHttpDataSource.Factory()
        dataSourceFactory.setUserAgent(Util.getUserAgent(application, application.packageName))
        // 缓存
        val cache = SimpleCache(
            application.cacheDir,
            LeastRecentlyUsedCacheEvictor(200 * 1024 * 1024L),
            null,
            null,
            false,
            false
        )
        val cacheDataSink = CacheDataSink.Factory().setCache(cache).setFragmentSize(Long.MAX_VALUE)
        val cacheDataSinkFactory = CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(dataSourceFactory)
            .setCacheReadDataSourceFactory(FileDataSource.Factory())
            .setCacheWriteDataSinkFactory(cacheDataSink)
        mediaSourceFactory = ProgressiveMediaSource.Factory(cacheDataSinkFactory)
    }

    fun createMediaSource(url: String): MediaSource {
        return mediaSourceFactory.createMediaSource(MediaItem.fromUri(Uri.parse(url)))
    }

    fun get(pageName: String): PageListPlay {
        var pageListPlay = hashMap[pageName]
        if (pageListPlay == null) {
            pageListPlay = PageListPlay()
            hashMap[pageName] = pageListPlay
        }
        return pageListPlay
    }

    fun release(pageName: String) {
        hashMap[pageName]?.let {
            hashMap.remove(pageName)
        }
    }
}