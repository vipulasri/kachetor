package com.vipulasri.kachetor.sample.shared

import com.vipulasri.kachetor.KachetorStorage
import com.vipulasri.kachetor.sample.shared.model.Rocket
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Created by Vipul Asri on 12/02/24.
 */

class SpacexApi {

    private val clientEngine by lazy { HttpClientEngineProvider() }

    private val client by lazy {
        HttpClient(clientEngine.engine) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }
            install(Logging) {
                logger = Logger.SIMPLE
                level = LogLevel.ALL
            }
            install(HttpCache) {
                publicStorage(KachetorStorage(50 * 1024 * 1024))
            }
            expectSuccess = true
        }
    }

    suspend fun getRockets(): List<Rocket>? {
        return try {
            client.get("https://api.spacexdata.com/v4/rockets")
                .body<List<Rocket>>()
                .sortedByDescending { it.firstFlight }
                .sortedBy { !it.active }
        } catch (exception: Exception) {
            println(exception)
            null
        }
    }

}