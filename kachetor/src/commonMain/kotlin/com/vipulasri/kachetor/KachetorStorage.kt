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

import io.ktor.client.plugins.cache.storage.CacheStorage
import io.ktor.client.plugins.cache.storage.CachedResponseData
import io.ktor.http.Url
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

class KachetorStorage internal constructor(
    private val persistentCache: PersistentCache? = null,
    private val inMemoryCache: InMemoryCache
) : CacheStorage {

    companion object {

        private const val TAG = "KachetorStorage"

        internal fun createPersistentCache(
            fileSystem: FileSystem,
            directoryPath: Path?,
            maxSize: Long,
            dispatcher: CoroutineDispatcher
        ): PersistentCache? {
            if (directoryPath == null) return null
            return PersistentCache(
                fileSystem, directoryPath, maxSize, dispatcher
            )
        }
    }

    constructor(maxSize: Long) : this(
        persistentCache = createPersistentCache(
            fileSystem = FileSystemProvider.fileSystem,
            directoryPath = FileSystemProvider.cacheDirectoryPath,
            maxSize = maxSize,
            dispatcher = Dispatchers.IO
        ),
        inMemoryCache = InMemoryCache()
    )

    constructor(directoryPath: String, maxSize: Long) : this(
        persistentCache = createPersistentCache(
            fileSystem = FileSystemProvider.fileSystem,
            directoryPath = directoryPath.toPath(),
            maxSize = maxSize,
            dispatcher = Dispatchers.IO
        ),
        inMemoryCache = InMemoryCache()
    )

    override suspend fun store(url: Url, data: CachedResponseData) {
        try {
            persistentCache?.store(url, data)
        } catch (exception: Exception) {
            println("$TAG, Error storing response persistently: ${exception.message}")
            inMemoryCache.store(url, data)
        }
    }

    override suspend fun find(url: Url, varyKeys: Map<String, String>): CachedResponseData? {
        return try {
            persistentCache?.find(url, varyKeys)
        } catch (exception: Exception) {
            println("$TAG, Error finding response from persistence: ${exception.message}")
            inMemoryCache.find(url, varyKeys)
        }
    }

    override suspend fun findAll(url: Url): Set<CachedResponseData> {
        return try {
            persistentCache?.findAll(url) ?: emptySet()
        } catch (exception: Exception) {
            println("$TAG, Error finding response from persistence: ${exception.message}")
            inMemoryCache.findAll(url)
        }
    }
}