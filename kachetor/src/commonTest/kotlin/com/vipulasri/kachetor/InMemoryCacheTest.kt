package com.vipulasri.kachetor

import io.ktor.client.plugins.cache.storage.CachedResponseData
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.headersOf
import io.ktor.util.date.GMTDate
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

/**
 * Created by Vipul Asri on 25/02/24.
 */

class InMemoryCacheTest {

    private fun getCachedData(
        url: String,
        varyKeys: Map<String, String> = emptyMap()
    ) = CachedResponseData(
        url = Url(url),
        statusCode = HttpStatusCode.OK,
        requestTime = GMTDate(),
        responseTime = GMTDate(),
        version = HttpProtocolVersion.HTTP_1_1,
        expires = GMTDate(),
        headers = headersOf(),
        varyKeys = varyKeys,
        body = ByteArray(0)
    )

    @Test
    fun testBasicCaching() = runTest {
        val inMemoryCache = InMemoryCache()
        val urlString = "http://www.example.com"

        inMemoryCache.store(Url(urlString), getCachedData(urlString))
        val cachedData = inMemoryCache.findAll(Url(urlString))
       assertEquals(cachedData.size, 1)
    }

    @Test
    fun testMultipleCaches() = runTest {
        val inMemoryCache = InMemoryCache()

        val urlString1 = "http://www.example.com"
        inMemoryCache.store(Url(urlString1), getCachedData(urlString1))

        val urlString2 = "http://www.example2.com"
        inMemoryCache.store(Url(urlString2), getCachedData(urlString2))

        val cachedData = inMemoryCache.findAll(Url(urlString2))
        assertEquals(cachedData.size, 1)
    }

    @Test
    fun testMultipleCachesForSameUrl() = runTest {
        val inMemoryCache = InMemoryCache()

        val urlString = "http://www.example.com"
        inMemoryCache.store(Url(urlString), getCachedData(urlString))
        inMemoryCache.store(Url(urlString), getCachedData(urlString, mapOf("key" to "value")))

        val allCachedData = inMemoryCache.findAll(Url(urlString))
        assertEquals(allCachedData.size, 2)

        val cachedData = inMemoryCache.find(Url(urlString), emptyMap())
        assertNotNull(cachedData)
    }

}