/*
 * Copyright 2024 Vipul Asri
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.vipulasri.kachetor

import com.mayakapps.kache.OkioFileKache
import io.ktor.client.plugins.cache.storage.CacheStorage
import io.ktor.client.plugins.cache.storage.CachedResponseData
import io.ktor.http.HeadersBuilder
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.util.date.GMTDate
import io.ktor.util.flattenEntries
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import okio.BufferedSink
import okio.BufferedSource
import okio.FileSystem
import okio.Path
import okio.buffer
import okio.use

internal class PersistentCache internal constructor(
    private val fileSystem: FileSystem,
    private val directory: Path,
    private val maxSize: Long,
    private val dispatcher: CoroutineDispatcher
) : CacheStorage {

    private suspend fun getOkioFileCache(): OkioFileKache {
        return OkioFileKache(
            directory = directory,
            maxSize = maxSize
        ) {
            fileSystem = this@PersistentCache.fileSystem
        }
    }

    override suspend fun store(url: Url, data: CachedResponseData) = withContext(dispatcher) {
        val cache = getOkioFileCache()
        val caches = mutableSetOf(data) // adding the new data initially to the set
        cache.read(url.toString())?.let { caches.addAll(it) }
        cache.write(url.toString(), caches.toList())
        cache.close()
    }

    override suspend fun find(url: Url, varyKeys: Map<String, String>): CachedResponseData? {
        val cache = getOkioFileCache()
        val data = cache.read(url.toString())
        cache.close()
        return data?.find {
            varyKeys.all { (key, value) -> it.varyKeys[key] == value }
        }
    }

    override suspend fun findAll(url: Url): Set<CachedResponseData> {
        val cache = getOkioFileCache()
        val data = cache.read(url.toString())?.toSet()
        cache.close()
        return data ?: emptySet()
    }

    private suspend fun OkioFileKache.write(
        key: String,
        caches: List<CachedResponseData>
    ): Path? {
        return put(key) { cachePath ->
            fileSystem.sink(cachePath).buffer().use { bufferedSink ->
                bufferedSink.writeInt(caches.size)
                val sortedCaches = caches.sortedByDescending { it.expires.timestamp }.toSet()
                for (cache in sortedCaches) {
                    writeCache(bufferedSink, cache)
                }
            }
            true
        }
    }

    private suspend fun OkioFileKache.read(
        key: String
    ): Set<CachedResponseData>? {
        return getIfAvailable(key)?.let { path ->
            fileSystem.read(path) {
                val requestsCount = this.readInt()
                val caches = mutableSetOf<CachedResponseData>()
                for (i in 0 until requestsCount) {
                    caches.add(readCache(this))
                }
                caches
            }
        }
    }

    private fun writeCache(bufferedSink: BufferedSink, cache: CachedResponseData) {
        bufferedSink.writeUtf8(cache.url.toString() + "\n")
        bufferedSink.writeInt(cache.statusCode.value)
        bufferedSink.writeUtf8(cache.statusCode.description + "\n")
        bufferedSink.writeUtf8(cache.version.toString() + "\n")
        val headers = cache.headers.flattenEntries()
        bufferedSink.writeInt(headers.size)
        for ((key, value) in headers) {
            bufferedSink.writeUtf8(key + "\n")
            bufferedSink.writeUtf8(value + "\n")
        }
        bufferedSink.writeLong(cache.requestTime.timestamp)
        bufferedSink.writeLong(cache.responseTime.timestamp)
        bufferedSink.writeLong(cache.expires.timestamp)
        bufferedSink.writeInt(cache.varyKeys.size)
        for ((key, value) in cache.varyKeys) {
            bufferedSink.writeUtf8(key + "\n")
            bufferedSink.writeUtf8(value + "\n")
        }
        bufferedSink.writeInt(cache.body.size)
        bufferedSink.write(cache.body)
    }

    private fun readCache(bufferedSource: BufferedSource): CachedResponseData {
        val url = bufferedSource.readUtf8Line()!!
        val status = HttpStatusCode(bufferedSource.readInt(), bufferedSource.readUtf8Line()!!)
        val version = HttpProtocolVersion.parse(bufferedSource.readUtf8Line()!!)
        val headersCount = bufferedSource.readInt()
        val headers = HeadersBuilder()
        for (j in 0 until headersCount) {
            val key = bufferedSource.readUtf8Line()!!
            val value = bufferedSource.readUtf8Line()!!
            headers.append(key, value)
        }
        val requestTime = GMTDate(bufferedSource.readLong())
        val responseTime = GMTDate(bufferedSource.readLong())
        val expirationTime = GMTDate(bufferedSource.readLong())
        val varyKeysCount = bufferedSource.readInt()
        val varyKeys = buildMap {
            for (j in 0 until varyKeysCount) {
                val key = bufferedSource.readUtf8Line()!!
                val value = bufferedSource.readUtf8Line()!!
                put(key, value)
            }
        }
        val bodyCount = bufferedSource.readInt()
        val body = ByteArray(bodyCount)
        bufferedSource.readFully(body)
        return CachedResponseData(
            url = Url(url),
            statusCode = status,
            requestTime = requestTime,
            responseTime = responseTime,
            version = version,
            expires = expirationTime,
            headers = headers.build(),
            varyKeys = varyKeys,
            body = body
        )
    }
}