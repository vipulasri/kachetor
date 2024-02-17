package com.vipulasri.kachetor.sample.shared.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Created by Vipul Asri on 15/02/24.
 */

@Serializable
data class Rocket(
    @SerialName("name") val name: String,
    @SerialName("active") val active: Boolean,
    @SerialName("first_flight") val firstFlight: String
)
