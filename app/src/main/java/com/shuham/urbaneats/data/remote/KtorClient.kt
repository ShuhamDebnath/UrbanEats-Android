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

    //const val BASE_URL = "http://localhost:3000/"// Localhost for Emulator
    const val BASE_URL = "http://10.0.2.2:3000/"// Localhost for Emulator
    //const val BASE_URL = "https://urbaneats-api.onrender.com/"// Real server render.com


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
            url(BASE_URL)
        }
    }
}