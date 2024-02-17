package com.vipulasri.kachetor.sample.shared

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp

/**
 * Created by Vipul Asri on 12/02/24.
 */

actual class HttpClientEngineProvider {
    actual val engine: HttpClientEngine = OkHttp.create()
}