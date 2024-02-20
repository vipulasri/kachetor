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

import android.content.Context
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import java.util.concurrent.atomic.AtomicReference

internal actual object FileSystemProvider {

    private var context = AtomicReference<Context>(null)

    fun initialize(context: Context?) {
        this.context.set(context)
    }

    actual val fileSystem: FileSystem
        get() = FileSystem.SYSTEM

    actual val cacheDirectoryPath: Path?
        get() = context.get().cacheDir.absolutePath.toPath()
}