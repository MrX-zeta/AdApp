package com.adapp.backend.Follower.Infrastructure.Routes

import com.adapp.backend.Follower.Infrastructure.Repositories.InMemoryFollowerRepository
import com.adapp.backend.Follower.Infrastructure.Controllers.KtorFollowerController
import com.adapp.backend.User.Domain.Exceptions.UserNotFoundError
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

fun Application.configureFollowerRouting(){
    // Inyectar repositorio usando Koin
    val repo: InMemoryFollowerRepository by inject()
    val controller = KtorFollowerController(repo)

    routing {
        get("/followers"){
            try{
                val followers = controller.getAll()
                call.respond(followers)
            }catch(e: Exception){
                call.application.environment.log.error("Error in GET /followers", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("message" to (e.message ?: "")))
            }
        }

        get("/follower/{id}/"){
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
                val follower = controller.getOneById(id)
                call.respond(follower)
            }catch(e: UserNotFoundError){
                call.respond(HttpStatusCode.NotFound, mapOf("message" to e.message))
            }
        }

        post("/follower/"){
            val payload = call.receive<CreateFollowerRequest>()
            controller.create(payload.id, payload.nombre, payload.correo, payload.contrasena, payload.rol)
            call.respond(HttpStatusCode.Created, mapOf("message" to "Artist created"))
        }

        put("/follower/{id}/"){
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

            val payload = call.receive<EditFollowerRequest>()
            try{
                controller.edit(oldId, payload.id, payload.nombre, payload.correo, payload.contrasena, payload.rol)
                call.respond(HttpStatusCode.NoContent)
            }catch(e: UserNotFoundError){
                call.respond(HttpStatusCode.NotFound, mapOf("message" to e.message))
            }catch(e: IllegalArgumentException){
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to e.message))
            }
        }

        delete("/follower/{id}"){
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
private data class CreateFollowerRequest(
    val id: Int,
    val nombre: String,
    val correo: String,
    val contrasena: String,
    val rol: String
)

@Serializable
private data class EditFollowerRequest(
    val id: Int,
    val nombre: String,
    val correo: String,
    val contrasena: String,
    val rol: String
)
