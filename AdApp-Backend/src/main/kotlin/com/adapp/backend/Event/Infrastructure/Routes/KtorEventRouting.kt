package com.adapp.backend.Event.Infrastructure.Routes

import com.adapp.backend.Event.Domain.Exceptions.EventNotFoundError
import com.adapp.backend.Event.Infrastructure.Controllers.KtorEventController
import com.adapp.backend.Event.Infrastructure.Repositories.InMemoryEventRepository
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
import java.util.Date

fun Application.configureEventRouting() {
    val repo = InMemoryEventRepository()
    val controller = KtorEventController(repo)

    routing {

        get("/event") {
            try {
                call.respond(controller.getAllEvents())
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("message" to (e.message ?: "")))
            }
        }

        get("/event/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Id inválido"))
                return@get
            }
            try {
                call.respond(HttpStatusCode.OK, controller.getOneById(id))
            } catch (e: EventNotFoundError) {
                call.respond(HttpStatusCode.NotFound, mapOf("message" to e.message))
            }
        }

        post("/event") {
            val payload = call.receive<CreateEventRequest>()
            try {
                controller.create(
                    payload.id,
                    payload.artistId,
                    payload.title,
                    Date(payload.dateEvent),
                    payload.description,
                    payload.status
                )
                call.respond(HttpStatusCode.Created, mapOf("message" to "Event creado"))
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.Conflict, mapOf("message" to e.message))
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf("message" to (e.message ?: "")))
            }
        }

        put("/event/{id}") {
            val idPath = call.parameters["id"]?.toIntOrNull()
            if (idPath == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Id inválido"))
                return@put
            }
            val payload = call.receive<EditEventRequest>()
            try {
                controller.edit(
                    idPath,
                    payload.artistId,
                    payload.title,
                    Date(payload.dateEvent),
                    payload.description,
                    payload.status
                )
                call.respond(HttpStatusCode.OK, mapOf("message" to "Event editado"))
            } catch (e: EventNotFoundError) {
                call.respond(HttpStatusCode.NotFound, mapOf("message" to e.message))
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.Conflict, mapOf("message" to e.message))
            }
        }

        delete("/event/{id}") {
            val id = call.parameters["id"]?.toIntOrNull()
            if (id == null) {
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Id inválido"))
                return@delete
            }
            try {
                controller.delete(id)
                call.respond(HttpStatusCode.OK, mapOf("message" to "Event eliminado"))
            } catch (e: EventNotFoundError) {
                call.respond(HttpStatusCode.NotFound, mapOf("message" to e.message))
            }
        }
    }
}

@Serializable
data class CreateEventRequest(
    val id: Int,
    val artistId: Int,
    val title: String,
    val description: String,
    val dateEvent: Long,
    val status: String
)

@Serializable
data class EditEventRequest(
    val artistId: Int,
    val title: String,
    val description: String,
    val dateEvent: Long,
    val status: String
)
