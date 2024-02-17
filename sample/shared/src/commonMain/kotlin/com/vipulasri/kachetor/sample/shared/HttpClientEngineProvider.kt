package com.vipulasri.kachetor.sample.shared

import io.ktor.client.engine.HttpClientEngine

/**
 * Created by Vipul Asri on 12/02/24.
 */

expect class HttpClientEngineProvider() {
    val engine: HttpClientEngine
}