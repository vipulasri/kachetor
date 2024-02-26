package com.vipulasri.kachetor

import io.ktor.client.plugins.cache.storage.CachedResponseData
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.headersOf
import io.ktor.util.date.GMTDate

/**
 * Created by Vipul Asri on 25/02/24.
 */

object TestData {

    fun getCachedResponseData(
        url: String,
        varyKeys: Map<String, String> = emptyMap(),
        expires: GMTDate = GMTDate()
    ) = CachedResponseData(
        url = Url(url),
        statusCode = HttpStatusCode.OK,
        requestTime = GMTDate(),
        responseTime = GMTDate(),
        version = HttpProtocolVersion.HTTP_1_1,
        expires = expires,
        headers = headersOf(),
        varyKeys = varyKeys,
        body = ByteArray(0)
    )

}