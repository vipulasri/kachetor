package com.vipulasri.kachetor

import io.ktor.http.Url
import io.ktor.util.date.GMTDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Created by Vipul Asri on 25/02/24.
 */

@OptIn(ExperimentalCoroutinesApi::class)
class PersistentCacheTest {

    private fun getPersistentCache() = PersistentCache(
        fileSystem = FakeFileSystem(),
        directory = "/cache/".toPath(),
        maxSize = 1024L,
        dispatcher = UnconfinedTestDispatcher()
    )

    @Test
    fun testBasicCaching() = runTest {
        val persistentCache = getPersistentCache()
        val urlString = "http://www.example.com"

        persistentCache.store(Url(urlString), TestData.getCachedResponseData(urlString))
        val cachedData = persistentCache.findAll(Url(urlString))
        assertEquals(cachedData.size, 1)
    }

    @Test
    fun testMultipleCaches() = runTest {
        val persistentCache = getPersistentCache()

        val urlString1 = "http://www.example.com"
        persistentCache.store(Url(urlString1), TestData.getCachedResponseData(urlString1))

        val urlString2 = "http://www.example2.com"
        persistentCache.store(Url(urlString2), TestData.getCachedResponseData(urlString2))

        val cachedData = persistentCache.findAll(Url(urlString2))
        assertEquals(cachedData.size, 1)
    }

    @Test
    fun testMultipleCachesForSameUrl() = runTest {
        val persistentCache = getPersistentCache()

        val urlString = "http://www.example.com"
        persistentCache.store(Url(urlString), TestData.getCachedResponseData(urlString))
        persistentCache.store(
            Url(urlString),
            TestData.getCachedResponseData(urlString, mapOf("key" to "value"))
        )

        val allCachedData = persistentCache.findAll(Url(urlString))
        assertEquals(allCachedData.size, 2)

        val cachedData = persistentCache.find(Url(urlString), emptyMap())
        assertNotNull(cachedData)
    }

    @Test
    fun testReturnsMostRecentStoredCache() = runTest {
        val persistentCache = getPersistentCache()

        val urlString = "http://www.example.com"
        persistentCache.store(
            Url(urlString),
            TestData.getCachedResponseData(url = urlString, expires = GMTDate(2000))
        )

        persistentCache.store(
            Url(urlString),
            TestData.getCachedResponseData(url = urlString, expires = GMTDate(1000))
        )

        val cachedData = persistentCache.find(Url(urlString), emptyMap())
        assertNotNull(cachedData)
        assertEquals(cachedData.expires.timestamp, 1000)
    }

}