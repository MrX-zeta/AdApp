package com.adapp.backend.Artist.Infrastructure.Routes

import com.adapp.backend.Artist.Infrastructure.Repositories.InMemoryArtistRepository
import com.adapp.backend.Artist.Infrastructure.Controllers.KtorArtistController
import com.adapp.backend.SocialMedia.Infrastructure.Repositories.InMemorySocialMediaRepository
import com.adapp.backend.SocialMedia.Domain.Models.SocialMedia
import com.adapp.backend.SocialMedia.Domain.Models.SocialMediaId
import com.adapp.backend.SocialMedia.Domain.Models.SocialMediaUrl
import com.adapp.backend.User.Domain.Models.UserId
import com.adapp.backend.User.Domain.Exceptions.UserNotFoundError
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import kotlinx.serialization.Serializable

fun Application.configureArtistRouting(
    repo: InMemoryArtistRepository,
    socialMediaRepo: InMemorySocialMediaRepository
){
    // Usar repositorio compartido recibido como parámetro
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
            controller.create(payload.id, payload.nombre, payload.correo, payload.contrasena, payload.rol, payload.fotoUrl, payload.contactNum, payload.description ?: "")
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
                controller.edit(oldId, payload.id, payload.nombre, payload.correo, payload.contrasena, payload.rol, payload.fotoUrl, payload.contactNum, payload.description ?: "")

                // Manejar redes sociales
                val artistId = payload.id

                // Helper para generar siguiente ID (recalcula cada vez)
                fun nextId(): Int {
                    val currentSocialMedia = socialMediaRepo.getAllSM()
                    val max = currentSocialMedia.maxOfOrNull { it.SocialMediaId.value } ?: 0
                    return max + 1
                }

                // Instagram
                val instagramEntry = socialMediaRepo.getAllSM().find {
                    it.artistId.value == artistId && it.url.value.startsWith("instagram:", ignoreCase = true)
                }
                if (!payload.instagram.isNullOrBlank()) {
                    val url = "instagram:${payload.instagram.trim()}"
                    if (instagramEntry != null) {
                        socialMediaRepo.edit(SocialMedia(instagramEntry.SocialMediaId, instagramEntry.artistId, SocialMediaUrl(url)))
                    } else {
                        socialMediaRepo.create(SocialMedia(SocialMediaId(nextId()), UserId(artistId), SocialMediaUrl(url)))
                    }
                } else if (instagramEntry != null) {
                    socialMediaRepo.delete(instagramEntry.SocialMediaId)
                }

                // Facebook
                val facebookEntry = socialMediaRepo.getAllSM().find {
                    it.artistId.value == artistId && it.url.value.startsWith("facebook:", ignoreCase = true)
                }
                if (!payload.facebook.isNullOrBlank()) {
                    val url = "facebook:${payload.facebook.trim()}"
                    if (facebookEntry != null) {
                        socialMediaRepo.edit(SocialMedia(facebookEntry.SocialMediaId, facebookEntry.artistId, SocialMediaUrl(url)))
                    } else {
                        socialMediaRepo.create(SocialMedia(SocialMediaId(nextId()), UserId(artistId), SocialMediaUrl(url)))
                    }
                } else if (facebookEntry != null) {
                    socialMediaRepo.delete(facebookEntry.SocialMediaId)
                }

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
    val contactNum: String,
    val instagram: String? = null,
    val facebook: String? = null,
    val description: String? = null
)

@Serializable
private data class EditArtistRequest(
    val id: Int,
    val nombre: String,
    val correo: String,
    val contrasena: String,
    val rol: String,
    val fotoUrl: String,
    val contactNum: String,
    val instagram: String? = null,
    val facebook: String? = null,
    val description: String? = null
)
