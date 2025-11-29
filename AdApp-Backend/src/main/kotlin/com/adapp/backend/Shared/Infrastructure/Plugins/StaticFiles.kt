package com.adapp.backend.Shared.Infrastructure.Plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Application.configureStaticFiles() {
    // Crear directorios si no existen
    val uploadsDir = File("uploads")
    val imagesDir = File("uploads/images")
    val audioDir = File("uploads/audio")

    uploadsDir.mkdirs()
    imagesDir.mkdirs()
    audioDir.mkdirs()

    routing {
        // Servir archivos estáticos desde /uploads
        staticFiles("/uploads", File("uploads"))

        // Endpoint para verificar que el servicio de archivos funciona
        get("/uploads/test") {
            call.respondText("File service is working!", ContentType.Text.Plain)
        }
    }
}

