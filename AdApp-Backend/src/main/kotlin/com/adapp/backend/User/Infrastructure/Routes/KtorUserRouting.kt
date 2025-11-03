package com.adapp.backend.User.Infrastructure.Routes

import com.adapp.backend.User.Infrastructure.Repositories.InMemoryUserRepository
import com.adapp.backend.User.Infrastructure.Controllers.KtorUserController
import com.adapp.backend.User.Domain.Exceptions.UserNotFoundError
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.Serializable

fun Application.configureRouting(){
    val userRepo = InMemoryUserRepository()
    val controller = KtorUserController(userRepo)

    install(ContentNegotiation) {
        json()
    }

    routing {
        get("/user/"){
            val users = controller.getAll()
            call.respond(users)
        }

        get("/user/{id}/"){
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
                val user = controller.getOneById(id)
                call.respond(user)
            }catch(e: UserNotFoundError){
                call.respond(HttpStatusCode.NotFound, mapOf("message" to e.message))
            }
        }

        post("/user/"){
            val payload = call.receive<CreateUserRequest>()
            controller.create(payload.id, payload.name, payload.email, payload.password, payload.role)
            call.respond(HttpStatusCode.Created, mapOf("message" to "User created"))
        }

        put("/user/"){
            val payload = call.receive<EditUserRequest>()
            try{
                controller.edit(payload.id, payload.name, payload.email, payload.password, payload.role)
                call.respond(HttpStatusCode.NoContent)
            }catch(e: UserNotFoundError){
                call.respond(HttpStatusCode.NotFound, mapOf("message" to e.message))
            }
        }

        delete("/user/{id}"){
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
private data class CreateUserRequest(
    val id: Int,
    val name: String,
    val email: String,
    val password: String,
    val role: String
)

@Serializable
private data class EditUserRequest(
    val id: Int,
    val name: String,
    val email: String,
    val password: String,
    val role: String
)
