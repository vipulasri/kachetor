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

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.pm.ProviderInfo
import android.database.Cursor
import android.net.Uri

class KachetorContentProvider : ContentProvider() {

    private val EMPTY_APPLICATION_ID_PROVIDER_AUTHORITY =
        "com.vipulasri.kachetor.KachetorContentProvider"

    override fun attachInfo(context: Context, info: ProviderInfo) {

        // Sanity check our security
        if (info.exported) {
            throw SecurityException("Provider must not be exported")
        }

        // So if the authorities equal the library internal ones, the developer forgot to set his applicationId
        check(EMPTY_APPLICATION_ID_PROVIDER_AUTHORITY != info.authority) {
            ("Incorrect provider authority in manifest. Most likely due to a missing applicationId variable in application's build.gradle.")
        }

        super.attachInfo(context, info)
    }

    override fun onCreate(): Boolean {
        FileSystemProvider.initialize(context)
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        return null
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        return 0
    }

}