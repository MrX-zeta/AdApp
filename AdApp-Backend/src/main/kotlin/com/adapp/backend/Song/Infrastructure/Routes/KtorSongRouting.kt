package com.adapp.backend.Song.Infrastructure.Routes

import com.adapp.backend.Song.Domain.Exceptions.SongNotFoundError
import com.adapp.backend.Song.Infrastructure.Controllers.KtorSongController
import com.adapp.backend.Song.Infrastructure.Repositories.InMemorySongRepository
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable

fun Application.configureSongRouting() {
    val repo = InMemorySongRepository()
    val controller = KtorSongController(repo)

    routing {

        get("/song") {
            try {
                call.respond(controller.getAll())
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("message" to (e.message ?: "")))
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

        post("/song") {
            val payload = call.receive<CreateSongRequest>()
            try {
                controller.create(payload.id, payload.artistId, payload.title, payload.url)
                call.respond(HttpStatusCode.Created, mapOf("message" to "Song creada"))
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
    val url: String
)

@Serializable
data class EditSongRequest(
    val id: Int,
    val artistId: Int,
    val title: String,
    val url: String
)
