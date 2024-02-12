package com.vipulasri.kachetor.sample.shared

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform