package com.adapp.backend.User.Infrastructure.Routes

import com.adapp.backend.User.Domain.Repositories.UserRepository
import com.adapp.backend.User.Infrastructure.Controllers.KtorUserController
import com.adapp.backend.User.Domain.Exceptions.UserNotFoundError
import com.adapp.backend.Artist.Domain.Repositories.ArtistRepository
import com.adapp.backend.Artist.Infrastructure.Controllers.KtorArtistController
import com.adapp.backend.Follower.Domain.Repsitories.FollowerRepository
import com.adapp.backend.Follower.Infrastructure.Controllers.KtorFollowerController
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

fun Application.configureRouting(){
    // Inyectar repositorios usando Koin (ahora usa PostgreSQL)
    val userRepo: UserRepository by inject()
    val artistRepo: ArtistRepository by inject()
    val followerRepo: FollowerRepository by inject()

    val controller = KtorUserController(userRepo)
    val artistController = KtorArtistController(artistRepo)
    val followerController = KtorFollowerController(followerRepo)

    // ContentNegotiation se instala en configureSerialization() a nivel de aplicación

    routing {
        // Register endpoint
        post("/auth/register"){
            try {
                val registerData = call.receive<RegisterRequest>()

                // Verificar si el email ya existe
                val allUsers = controller.getAll()
                val existingUser = allUsers.find { it.correo == registerData.email }

                if(existingUser != null){
                    call.respond(HttpStatusCode.Conflict, mapOf(
                        "error" to "El correo ${registerData.email} ya está registrado",
                        "message" to "Por favor, utiliza otro correo electrónico"
                    ))
                    return@post
                }

                // Generar ID automáticamente
                val newId = if(allUsers.isEmpty()) 1 else allUsers.maxOf { it.id } + 1
                
                // Crear usuario en tabla users
                controller.create(
                    id = newId,
                    name = registerData.username,
                    email = registerData.email,
                    passwd = registerData.password,
                    rol = registerData.userType
                )

                // Si es artista, también crear registro en artists (solo tabla artists, no users otra vez)
                if (registerData.userType.equals("artist", ignoreCase = true)) {
                    // Crear registro SOLO en la tabla artists
                    artistController.createArtistProfile(
                        id = newId,
                        fotoUrl = "",
                        contactNum = "",
                        description = ""
                    )
                }
                // Si es follower, también crear registro en followers
                else if (registerData.userType.equals("follower", ignoreCase = true)) {
                    // Crear registro SOLO en la tabla followers
                    followerController.createFollowerProfile(newId)
                }

                // Generar token JWT (mock)
                val token = "mock-jwt-token-$newId"

                val response = LoginResponse(
                    token = token,
                    user = UserData(
                        id = newId,
                        nombre = registerData.username,
                        correo = registerData.email,
                        rol = registerData.userType
                    )
                )

                call.respond(HttpStatusCode.Created, response)

            } catch (e: org.jetbrains.exposed.exceptions.ExposedSQLException) {
                // Manejar error de llave duplicada de PostgreSQL
                if (e.message?.contains("users_correo_key") == true) {
                    call.respond(HttpStatusCode.Conflict, mapOf(
                        "error" to "El correo ya está registrado en el sistema",
                        "message" to "Este correo electrónico ya está en uso. Por favor, inicia sesión o usa otro correo."
                    ))
                } else {
                    call.respond(HttpStatusCode.InternalServerError, mapOf(
                        "error" to "Error al registrar usuario",
                        "message" to "Ha ocurrido un error en el servidor. Inténtalo más tarde."
                    ))
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, mapOf(
                    "error" to "Error inesperado",
                    "message" to (e.message ?: "Error desconocido")
                ))
            }
        }

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
            controller.create(
                id = payload.id,
                name = payload.name,
                email = payload.email,
                passwd = payload.password,
                rol = payload.role
            )
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
                controller.edit(
                    oldId = oldId,
                    newId = payload.id,
                    name = payload.name,
                    email = payload.email,
                    passwd = payload.password,
                    rol = payload.role
                )
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
private data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val userType: String
)

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
