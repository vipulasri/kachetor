package com.vipulasri.kachetor

import io.ktor.http.Url
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Created by Vipul Asri on 25/02/24.
 */

class InMemoryCacheTest {

    @Test
    fun testBasicCaching() = runTest {
        val inMemoryCache = InMemoryCache()
        val urlString = "http://www.example.com"

        inMemoryCache.store(Url(urlString), TestData.getCachedResponseData(urlString))
        val cachedData = inMemoryCache.findAll(Url(urlString))
        assertEquals(cachedData.size, 1)
    }

    @Test
    fun testMultipleCaches() = runTest {
        val inMemoryCache = InMemoryCache()

        val urlString1 = "http://www.example.com"
        inMemoryCache.store(Url(urlString1), TestData.getCachedResponseData(urlString1))

        val urlString2 = "http://www.example2.com"
        inMemoryCache.store(Url(urlString2), TestData.getCachedResponseData(urlString2))

        val cachedData = inMemoryCache.findAll(Url(urlString2))
        assertEquals(cachedData.size, 1)
    }

    @Test
    fun testMultipleCachesForSameUrl() = runTest {
        val inMemoryCache = InMemoryCache()

        val urlString = "http://www.example.com"
        inMemoryCache.store(Url(urlString), TestData.getCachedResponseData(urlString))
        inMemoryCache.store(
            Url(urlString),
            TestData.getCachedResponseData(urlString, mapOf("key" to "value"))
        )

        val allCachedData = inMemoryCache.findAll(Url(urlString))
        assertEquals(allCachedData.size, 2)

        val cachedData = inMemoryCache.find(Url(urlString), emptyMap())
        assertNotNull(cachedData)
    }

}