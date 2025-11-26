package com.adapp.backend.SocialMedia.Infrastructure.Routes

import com.adapp.backend.SocialMedia.Domain.Exceptions.SocialMediaNotFoundError
import com.adapp.backend.SocialMedia.Infrastructure.Controllers.KtorSocialMediaController
import com.adapp.backend.SocialMedia.Infrastructure.Repositories.InMemorySocialMediaRepository
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
import org.koin.ktor.ext.inject

fun Application.configureRouting(){
    // Inyectar repositorio usando Koin
    val SMRepo: InMemorySocialMediaRepository by inject()

    val controller = KtorSocialMediaController(SMRepo)

    routing {
        get("/sm"){
            try {
                val socialMedias = controller.getAll()
                call.respond(socialMedias)
            }catch (e:Exception){
                call.application.environment.log.error("Error in GET /sm", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("message" to (e.message ?: "")))
            }
        }

        get("/sm/{id}"){
            val idParam = call.parameters["id"]
            if(idParam == null){
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Missing id"))
                return@get
            }
            val id = idParam.toIntOrNull()
            if(id == null){
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid id"))
                return@get
            }

            try{
                val socialMedia = controller.getOneById(id)
                call.respond(HttpStatusCode.OK, socialMedia)
            }catch (e: SocialMediaNotFoundError){
                call.respond(HttpStatusCode.NotFound, mapOf("message" to e.toString()))
            }
        }

        get("/socialMedia/artist/{artistId}/"){
            val artistIdParam = call.parameters["artistId"]
            if(artistIdParam == null){
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Missing artistId"))
                return@get
            }
            val artistId = artistIdParam.toIntOrNull()
            if(artistId == null){
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid artistId"))
                return@get
            }

            try{
                val socialMedias = controller.getByArtistId(artistId)
                call.respond(HttpStatusCode.OK, socialMedias)
            }catch(e: Exception){
                call.application.environment.log.error("Error in GET /socialMedia/artist/{artistId}/", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("message" to (e.message ?: "")))
            }
        }

        post("/sm/"){
            val payload = call.receive<CreateSMRequest>()
            controller.create(payload.id, payload.artistId, payload.url)
        }

        put("/sm/{id}/"){
            val idParam = call.parameters["id"]
            if (idParam == null){
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Missing id"))
                return@put
            }
            val oldId = idParam.toIntOrNull()
            if (oldId == null){
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid id"))
                return@put
            }
            val payload = call.receive<EditSMRequest>()
            try {
                controller.edit(oldId, payload.id, payload.artistId.toInt(), payload.url)
            }catch (e: SocialMediaNotFoundError){
                call.respond(HttpStatusCode.NotFound, mapOf("message" to e.toString()))
            }catch (e: IllegalArgumentException){
                call.respond(HttpStatusCode.Conflict, mapOf("message" to e.message.toString()))
            }
        }

        delete("/sm/{id}"){
            val idParam = call.parameters["id"]
            if (idParam == null){
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Missing id"))
                return@delete
            }
            val id = idParam.toIntOrNull()
            if (id == null){
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid id"))
                return@delete
            }
            try {
                controller.delete(id)
            }catch (e: SocialMediaNotFoundError){
                call.respond(HttpStatusCode.NotFound, mapOf("message" to e.toString()))
            }
        }
    }
}

@Serializable
private data class CreateSMRequest(
    val id: Int,
    val artistId: Int,
    val url: String
)

@Serializable
private data class EditSMRequest(
    val id: Int,
    val artistId: String,
    val url: String
)