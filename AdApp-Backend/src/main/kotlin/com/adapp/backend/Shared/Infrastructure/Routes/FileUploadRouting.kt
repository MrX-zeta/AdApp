package com.adapp.backend.Shared.Infrastructure.Routes

import com.adapp.backend.Shared.Infrastructure.Services.FileUploadService
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureFileUploadRouting() {
    val fileService: FileUploadService by inject()

    routing {
        // POST /upload/image - Subir imagen
        post("/upload/image") {
            try {
                val multipart = call.receiveMultipart()
                var fileUrl: String? = null
                var invalidType = false

                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FileItem -> {
                            if (fileService.isValidImage(part.contentType?.toString())) {
                                fileUrl = fileService.saveImage(part)
                            } else {
                                invalidType = true
                            }
                        }
                        else -> {}
                    }
                    part.dispose()
                }

                when {
                    invalidType -> call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("message" to "Invalid file type. Only images are allowed.")
                    )
                    fileUrl != null -> call.respond(
                        HttpStatusCode.Created,
                        mapOf(
                            "message" to "Image uploaded successfully",
                            "imageUrl" to fileUrl
                        )
                    )
                    else -> call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("message" to "No file provided")
                    )
                }
            } catch (e: Exception) {
                call.application.environment.log.error("Error uploading image", e)
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("message" to "Error uploading image: ${e.message}")
                )
            }
        }

        // POST /upload/audio - Subir audio
        post("/upload/audio") {
            try {
                val multipart = call.receiveMultipart()
                var fileUrl: String? = null
                var invalidType = false

                multipart.forEachPart { part ->
                    when (part) {
                        is PartData.FileItem -> {
                            if (fileService.isValidAudio(part.contentType?.toString())) {
                                fileUrl = fileService.saveAudio(part)
                            } else {
                                invalidType = true
                            }
                        }
                        else -> {}
                    }
                    part.dispose()
                }

                when {
                    invalidType -> call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("message" to "Invalid file type. Only audio files are allowed.")
                    )
                    fileUrl != null -> call.respond(
                        HttpStatusCode.Created,
                        mapOf(
                            "message" to "Audio uploaded successfully",
                            "audioUrl" to fileUrl
                        )
                    )
                    else -> call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("message" to "No file provided")
                    )
                }
            } catch (e: Exception) {
                call.application.environment.log.error("Error uploading audio", e)
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("message" to "Error uploading audio: ${e.message}")
                )
            }
        }

        // DELETE /upload - Eliminar archivo
        delete("/upload") {
            try {
                val filePath = call.request.queryParameters["path"]
                if (filePath.isNullOrBlank()) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("message" to "File path is required")
                    )
                    return@delete
                }

                val deleted = fileService.deleteFile(filePath)
                if (deleted) {
                    call.respond(
                        HttpStatusCode.OK,
                        mapOf("message" to "File deleted successfully")
                    )
                } else {
                    call.respond(
                        HttpStatusCode.NotFound,
                        mapOf("message" to "File not found")
                    )
                }
            } catch (e: Exception) {
                call.application.environment.log.error("Error deleting file", e)
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("message" to "Error deleting file: ${e.message}")
                )
            }
        }
    }
}

