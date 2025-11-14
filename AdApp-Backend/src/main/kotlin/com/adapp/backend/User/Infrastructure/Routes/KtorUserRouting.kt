package com.adapp.backend.User.Infrastructure.Routes

import com.adapp.backend.User.Infrastructure.Repositories.InMemoryUserRepository
import com.adapp.backend.User.Infrastructure.Controllers.KtorUserController
import com.adapp.backend.User.Domain.Exceptions.UserNotFoundError
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import kotlinx.serialization.Serializable

fun Application.configureRouting(){
    val userRepo = InMemoryUserRepository()
    val controller = KtorUserController(userRepo)

    // ContentNegotiation se instala en configureSerialization() a nivel de aplicación

    routing {
        // Login endpoint
        post("/auth/login"){
            val credentials = call.receive<LoginRequest>()
            
            // Buscar usuario por email
            val allUsers = controller.getAll()
            val user = allUsers.find { it.correo == credentials.email }
            
            if(user == null || user.contrasena != credentials.password){
                call.respond(HttpStatusCode.Unauthorized, mapOf("message" to "Credenciales inválidas"))
                return@post
            }
            
            // Generar token JWT (por ahora mock)
            val token = "mock-jwt-token-${user.id}"
            
            val response = LoginResponse(
                token = token,
                user = UserData(
                    id = user.id,
                    nombre = user.nombre,
                    correo = user.correo,
                    rol = user.rol
                )
            )
            
            call.respond(response)
        }

        get("/users"){
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

        put("/user/{id}/"){
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

            val payload = call.receive<EditUserRequest>()
            try{
                controller.edit(oldId, payload.id, payload.name, payload.email, payload.password, payload.role)
                call.respond(HttpStatusCode.NoContent)
            }catch(e: UserNotFoundError){
                call.respond(HttpStatusCode.NotFound, mapOf("message" to e.message))
            }catch(e: IllegalArgumentException){
                call.respond(HttpStatusCode.BadRequest, mapOf("message" to e.message))
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
private data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
private data class LoginResponse(
    val token: String,
    val user: UserData
)

@Serializable
private data class UserData(
    val id: Int,
    val nombre: String,
    val correo: String,
    val rol: String
)

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
