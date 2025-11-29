package com.adapp.backend.Shared.Infrastructure.Services

import io.ktor.http.content.*
import io.ktor.utils.io.*
import java.io.File
import java.util.*

class FileUploadService {
    private val uploadsDir = "uploads"
    private val imagesDir = "$uploadsDir/images"
    private val audioDir = "$uploadsDir/audio"

    init {
        // Crear directorios si no existen
        File(uploadsDir).mkdirs()
        File(imagesDir).mkdirs()
        File(audioDir).mkdirs()
    }

    /**
     * Guarda una imagen y retorna la URL relativa
     */
    suspend fun saveImage(fileItem: PartData.FileItem): String {
        val fileName = generateUniqueFileName(fileItem.originalFileName ?: "image.jpg")
        val file = File("$imagesDir/$fileName")

        val byteChannel = fileItem.provider()
        file.outputStream().use { output ->
            val buffer = ByteArray(8192)
            while (true) {
                val read = byteChannel.readAvailable(buffer)
                if (read <= 0) break
                output.write(buffer, 0, read)
            }
        }

        return "/uploads/images/$fileName"
    }

    /**
     * Guarda un archivo de audio y retorna la URL relativa
     */
    suspend fun saveAudio(fileItem: PartData.FileItem): String {
        val fileName = generateUniqueFileName(fileItem.originalFileName ?: "audio.mp3")
        val file = File("$audioDir/$fileName")

        val byteChannel = fileItem.provider()
        file.outputStream().use { output ->
            val buffer = ByteArray(8192)
            while (true) {
                val read = byteChannel.readAvailable(buffer)
                if (read <= 0) break
                output.write(buffer, 0, read)
            }
        }

        return "/uploads/audio/$fileName"
    }

    /**
     * Elimina un archivo dado su path
     */
    fun deleteFile(filePath: String): Boolean {
        val file = File(filePath.removePrefix("/"))
        return if (file.exists()) {
            file.delete()
        } else {
            false
        }
    }

    /**
     * Genera un nombre único para el archivo
     */
    private fun generateUniqueFileName(originalFileName: String): String {
        val extension = originalFileName.substringAfterLast(".", "")
        val timestamp = System.currentTimeMillis()
        val randomId = UUID.randomUUID().toString().take(8)
        return "${timestamp}_${randomId}.$extension"
    }

    /**
     * Valida que el archivo sea una imagen
     */
    fun isValidImage(contentType: String?): Boolean {
        return contentType?.startsWith("image/") == true
    }

    /**
     * Valida que el archivo sea audio
     */
    fun isValidAudio(contentType: String?): Boolean {
        return contentType?.startsWith("audio/") == true
    }
}

