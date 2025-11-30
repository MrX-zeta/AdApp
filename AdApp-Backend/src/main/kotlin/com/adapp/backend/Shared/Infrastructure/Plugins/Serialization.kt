package com.adapp.backend.Shared.Infrastructure.Plugins

import io.ktor.server.application.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
        // Aumentar límite de tamaño para soportar archivos de audio (50MB)
        // Los archivos de audio pueden ser grandes (mp3, wav, etc.)
        val maxContentLength = 50L * 1024 * 1024 // 50 MB
    }

    install(CORS) {
        allowHost("localhost:4200", schemes = listOf("http", "https"))
        allowHost("127.0.0.1:4200", schemes = listOf("http", "https"))
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowCredentials = true
    }
}
