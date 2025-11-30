package com.adapp.backend.Song.Infrastructure.Routes

import com.adapp.backend.Song.Domain.Exceptions.SongNotFoundError
import com.adapp.backend.Song.Domain.Repositories.SongRepository
import com.adapp.backend.Song.Infrastructure.Controllers.KtorSongController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

fun Application.configureSongRouting() {
    // Inyectar repositorio usando Koin
    val songRepo: SongRepository by inject()
    val controller = KtorSongController(songRepo)

    routing {
        // Alias plural para compatibilidad con frontend
        get("/songs") {
            try {
                call.respond(controller.getAll())
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("message" to (e.message ?: "")))
            }
        }

        get("/song") {
            try {
                call.respond(controller.getAll())
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("message" to (e.message ?: "")))
            }
        }

        // Alias plural
        get("/songs/{id}/") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Id inválido"))
                return@get
            }
            try {
                val song = controller.getOneById(id)
                call.respond(HttpStatusCode.OK, song)
            } catch (e: SongNotFoundError) {
                call.respond(HttpStatusCode.NotFound, mapOf("message" to e.message))
            }
        }

        get("/song/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Id inválido"))
                return@get
            }
            try {
                val song = controller.getOneById(id)
                call.respond(HttpStatusCode.OK, song)
            } catch (e: SongNotFoundError) {
                call.respond(HttpStatusCode.NotFound, mapOf("message" to e.message))
            }
        }

        // Alias plural
        post("/songs") {
            val payload = call.receive<CreateSongRequest>()
            try {
                controller.create(payload.id, payload.artistId, payload.title, payload.url, payload.dateUploaded ?: System.currentTimeMillis())
                call.respond(HttpStatusCode.Created, mapOf("message" to "Song creada"))
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.Conflict, mapOf("message" to e.message))
            }
        }

        post("/song") {
            val payload = call.receive<CreateSongRequest>()
            try {
                controller.create(payload.id, payload.artistId, payload.title, payload.url, payload.dateUploaded ?: System.currentTimeMillis())
                call.respond(HttpStatusCode.Created, mapOf("message" to "Song creada"))
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.Conflict, mapOf("message" to e.message))
            }
        }

        // Alias plural
        put("/songs/{id}/") {
            val oldId = call.parameters["id"]?.toIntOrNull()
            if (oldId == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Id inválido"))
                return@put
            }
            val payload = call.receive<EditSongRequest>()
            try {
                controller.edit(payload.id, payload.artistId, payload.title, payload.url)
                call.respond(HttpStatusCode.OK, mapOf("message" to "Song editada"))
            } catch (e: SongNotFoundError) {
                call.respond(HttpStatusCode.NotFound, mapOf("message" to e.message))
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.Conflict, mapOf("message" to e.message))
            }
        }

        put("/song/{id}") {
            val oldId = call.parameters["id"]?.toIntOrNull()
            if (oldId == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Id inválido"))
                return@put
            }
            val payload = call.receive<EditSongRequest>()
            try {
                controller.edit(payload.id, payload.artistId, payload.title, payload.url)
                call.respond(HttpStatusCode.OK, mapOf("message" to "Song editada"))
            } catch (e: SongNotFoundError) {
                call.respond(HttpStatusCode.NotFound, mapOf("message" to e.message))
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.Conflict, mapOf("message" to e.message))
            }
        }

        // Alias plural
        delete("/songs/{id}/") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Id inválido"))
                return@delete
            }
            try {
                controller.delete(id)
                call.respond(HttpStatusCode.OK, mapOf("message" to "Song eliminada"))
            } catch (e: SongNotFoundError) {
                call.respond(HttpStatusCode.NotFound, mapOf("message" to e.message))
            }
        }

        delete("/song/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Id inválido"))
                return@delete
            }
            try {
                controller.delete(id)
                call.respond(HttpStatusCode.OK, mapOf("message" to "Song eliminada"))
            } catch (e: SongNotFoundError) {
                call.respond(HttpStatusCode.NotFound, mapOf("message" to e.message))
            }
        }
    }
}

@Serializable
data class CreateSongRequest(
    val id: Int,
    val artistId: Int,
    val title: String,
    val url: String,
    val dateUploaded: Long? = null
)

@Serializable
data class EditSongRequest(
    val id: Int,
    val artistId: Int,
    val title: String,
    val url: String,
    val dateUploaded: Long? = null
)
