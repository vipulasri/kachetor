package com.vipulasri.kachetor

import io.ktor.client.plugins.cache.storage.CacheStorage
import io.ktor.http.Url
import io.mockative.Mock
import io.mockative.any
import io.mockative.classOf
import io.mockative.coEvery
import io.mockative.coVerify
import io.mockative.mock
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Created by Vipul Asri on 25/02/24.
 */

class KachetorStorageTest {

    @Mock
    private val persistentCache = mock(classOf<CacheStorage>())

    @Mock
    private val inMemoryCache = mock(classOf<CacheStorage>())

    @Test
    fun testPersistentCaching() = runTest {
        val kachetorStorage = KachetorStorage(
            persistentCache = persistentCache,
            inMemoryCache = inMemoryCache
        )

        val urlString = "http://www.example.com"

        coEvery { kachetorStorage.findAll(any()) }.returns(
            setOf(
                TestData.getCachedResponseData(
                    urlString
                )
            )
        )

        kachetorStorage.store(Url(urlString), TestData.getCachedResponseData(urlString))
        val cachedData = kachetorStorage.findAll(Url(urlString))
        assertEquals(cachedData.size, 1)

        coVerify {
            persistentCache.store(
                Url(urlString),
                TestData.getCachedResponseData(urlString)
            )
        }.wasInvoked()
    }

    @Test
    fun shouldUseInMemoryCacheWhenPersistentCacheNull() = runTest {
        val kachetorStorage = KachetorStorage(
            persistentCache = null,
            inMemoryCache = inMemoryCache
        )

        val urlString = "http://www.example.com"

        kachetorStorage.store(Url(urlString), TestData.getCachedResponseData(urlString))

        coVerify {
            persistentCache.store(
                Url(urlString),
                TestData.getCachedResponseData(urlString)
            )
        }.wasNotInvoked()

        coVerify {
            inMemoryCache.store(
                Url(urlString),
                TestData.getCachedResponseData(urlString)
            )
        }.wasInvoked()
    }

    @Test
    fun shouldUseInMemoryCacheWhenPersistentCacheIsUnavailable() = runTest {
        val kachetorStorage = KachetorStorage(
            persistentCache = persistentCache,
            inMemoryCache = inMemoryCache
        )

        val urlString = "http://www.example.com"

        coEvery { persistentCache.store(any(), any()) }.throws(Exception("Cache is unavailable"))
        coEvery { persistentCache.find(any(), any()) }.throws(Exception("Cache is unavailable"))
        coEvery { persistentCache.findAll(any()) }.throws(Exception("Cache is unavailable"))

        coEvery { inMemoryCache.find(any(), any()) }.returns(
            TestData.getCachedResponseData(
                urlString
            )
        )
        coEvery { inMemoryCache.findAll(any()) }.returns(
            setOf(
                TestData.getCachedResponseData(
                    urlString
                )
            )
        )

        kachetorStorage.store(Url(urlString), TestData.getCachedResponseData(urlString))
        val caches = kachetorStorage.findAll(Url(urlString))
        assertEquals(caches.size, 1)

        val cache = kachetorStorage.find(Url(urlString), emptyMap())
        assertNotNull(cache)
        assertEquals(cache.url, Url(urlString))

        coVerify {
            inMemoryCache.store(
                Url(urlString),
                TestData.getCachedResponseData(urlString)
            )
        }.wasInvoked()

        coVerify {
            inMemoryCache.find(Url(urlString), emptyMap())
        }.wasInvoked()

        coVerify {
            inMemoryCache.findAll(Url(urlString))
        }.wasInvoked()
    }
}