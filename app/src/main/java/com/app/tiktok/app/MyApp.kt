package com.app.tiktok.app

import android.app.Application
import android.content.Context
import com.google.android.exoplayer2.database.DatabaseProvider
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@HiltAndroidApp
class MyApp: Application() {
    companion object {
        var simpleCache: SimpleCache? = null
        var context: Context? = null
        var executorService: ExecutorService? = null
    }

    override fun onCreate() {
        super.onCreate()
        executorService = Executors.newFixedThreadPool(6)

        context = this

        val leastRecentlyUsedCacheEvictor = LeastRecentlyUsedCacheEvictor(90 * 1024 * 1024)
        val databaseProvider: DatabaseProvider = ExoDatabaseProvider(this)

        if (simpleCache == null) {
            simpleCache = SimpleCache(cacheDir, leastRecentlyUsedCacheEvictor, databaseProvider)
        }
    }
}