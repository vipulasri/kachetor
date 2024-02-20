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

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSError
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask

internal actual object FileSystemProvider {

    private val manager by lazy { NSFileManager.defaultManager }

    actual val fileSystem: FileSystem
        get() = FileSystem.SYSTEM

    actual val cacheDirectoryPath: Path?
        get() {
            return getDirPath(NSCachesDirectory, true)
        }

    private fun getDirPath(directory: NSSearchPathDirectory, create: Boolean = false): Path? {
        val dirUrl = getDirUrl(directory, create)?.path
        return dirUrl?.toPath()
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun getDirUrl(directory: NSSearchPathDirectory, create: Boolean = false): NSURL? {
        memScoped {
            val error = alloc<ObjCObjectVar<NSError?>>()
            return manager.URLForDirectory(
                directory,
                NSUserDomainMask,
                null,
                create,
                error.ptr
            )?.standardizedURL
        }
    }
}