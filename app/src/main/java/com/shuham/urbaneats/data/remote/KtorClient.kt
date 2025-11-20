package com.shuham.urbaneats.data.remote


import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object KtorClient {


    val httpClient = HttpClient(OkHttp) {
        // 1. JSON Serialization Setup
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true // Crucial: Don't crash if backend adds extra fields
            })
        }

        // 2. Logging (View API calls in Logcat)
        install(Logging) {
            level = LogLevel.ALL
        }

        // 3. Default Base URL (Saves typing)
        defaultRequest {
            url("http://10.0.2.2:3000/") // Localhost for Emulator
            // url("http://192.168.1.X:3000/") // Use this if testing on Real Device (Your PC IP)
        }
    }
}