package com.adapp.backend.Artist.Infrastructure.Routes

import com.adapp.backend.Artist.Infrastructure.Repositories.InMemoryArtistRepository
import com.adapp.backend.Artist.Infrastructure.Controllers.KtorArtistController
import com.adapp.backend.User.Domain.Exceptions.UserNotFoundError
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import kotlinx.serialization.Serializable

fun Application.configureArtistRouting(){
    val repo = InMemoryArtistRepository()
    val controller = KtorArtistController(repo)

    routing {
        // Alias plural para compatibilidad
        get("/artists"){
            try{
                val artists = controller.getAll()
                call.respond(artists)
            }catch(e: Exception){
                call.application.environment.log.error("Error in GET /artists", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("message" to (e.message ?: "")))
            }
        }

        // Principal
        get("/artist/"){
            val artists = controller.getAll()
            call.respond(artists)
        }

        get("/artist/{id}/"){
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
                val artist = controller.getOneById(id)
                call.respond(artist)
            }catch(e: UserNotFoundError){
                call.respond(HttpStatusCode.NotFound, mapOf("message" to e.message))
            }
        }

        post("/artist/"){
            val payload = call.receive<CreateArtistRequest>()
            controller.create(payload.id, payload.nombre, payload.correo, payload.contrasena, payload.rol, payload.fotoUrl, payload.contactNum)
            call.respond(HttpStatusCode.Created, mapOf("message" to "Artist created"))
        }

        put("/artist/{id}/"){
            val idParam = call.parameters["id"]
            if(idParam == null){
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Missing id"))
                return@put
            }
            val oldId = idParam.toIntOrNull()
            if(oldId == null){
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid id"))
                return@put
            }

            val payload = call.receive<EditArtistRequest>()
            try{
                controller.edit(oldId, payload.id, payload.nombre, payload.correo, payload.contrasena, payload.rol, payload.fotoUrl, payload.contactNum)
                call.respond(HttpStatusCode.NoContent)
            }catch(e: UserNotFoundError){
                call.respond(HttpStatusCode.NotFound, mapOf("message" to e.message))
            }catch(e: IllegalArgumentException){
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to e.message))
            }
        }

        delete("/artist/{id}"){
            val idParam = call.parameters["id"]
            if(idParam == null){
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Missing id"))
                return@delete
            }
            val id = idParam.toIntOrNull()
            if(id == null){
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid id"))
                return@delete
            }
            try{
                controller.delete(id)
                call.respond(HttpStatusCode.NoContent)
            }catch(e: UserNotFoundError){
                call.respond(HttpStatusCode.NotFound, mapOf("message" to e.message))
            }
        }
    }
}

@Serializable
private data class CreateArtistRequest(
    val id: Int,
    val nombre: String,
    val correo: String,
    val contrasena: String,
    val rol: String,
    val fotoUrl: String,
    val contactNum: String
)

@Serializable
private data class EditArtistRequest(
    val id: Int,
    val nombre: String,
    val correo: String,
    val contrasena: String,
    val rol: String,
    val fotoUrl: String,
    val contactNum: String
)
