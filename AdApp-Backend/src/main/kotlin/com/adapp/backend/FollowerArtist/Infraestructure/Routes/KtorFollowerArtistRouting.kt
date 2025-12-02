package com.adapp.backend.FollowerArtist.Infraestructure.Routes

import com.adapp.backend.FollowerArtist.Infraestructure.Controllers.KtorFollowerArtistController
import com.adapp.backend.Artist.Domain.Repositories.ArtistRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Application.configureFollowerArtistRouting() {
    // Inyectar controller usando Koin (ahora usa PostgreSQL)
    val controller: KtorFollowerArtistController by inject()
    val artistRepo: ArtistRepository by inject()

    routing {
        // POST /follower/{followerId}/follow/{artistId} - Seguir a un artista
        post("/follower/{followerId}/follow/{artistId}") {
            try {
                val followerIdParam = call.parameters["followerId"]
                val artistIdParam = call.parameters["artistId"]

                if (followerIdParam == null || artistIdParam == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Missing follower or artist id"))
                    return@post
                }

                val followerId = followerIdParam.toIntOrNull()
                val artistId = artistIdParam.toIntOrNull()

                if (followerId == null || artistId == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid id"))
                    return@post
                }

                controller.followArtist(followerId, artistId)
                call.respond(HttpStatusCode.OK, mapOf("message" to "Now following artist"))
            } catch (e: Exception) {
                call.application.environment.log.error("Error following artist", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("message" to (e.message ?: "Error following artist")))
            }
        }

        // DELETE /follower/{followerId}/follow/{artistId} - Dejar de seguir a un artista
        delete("/follower/{followerId}/follow/{artistId}") {
            try {
                val followerIdParam = call.parameters["followerId"]
                val artistIdParam = call.parameters["artistId"]

                if (followerIdParam == null || artistIdParam == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Missing follower or artist id"))
                    return@delete
                }

                val followerId = followerIdParam.toIntOrNull()
                val artistId = artistIdParam.toIntOrNull()

                if (followerId == null || artistId == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid id"))
                    return@delete
                }

                controller.unfollowArtist(followerId, artistId)
                call.respond(HttpStatusCode.OK, mapOf("message" to "Unfollowed artist"))
            } catch (e: Exception) {
                call.application.environment.log.error("Error unfollowing artist", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("message" to (e.message ?: "Error unfollowing artist")))
            }
        }

        // GET /follower/{followerId}/following - Obtener artistas seguidos por un follower
        get("/follower/{followerId}/following") {
            try {
                val followerIdParam = call.parameters["followerId"]

                if (followerIdParam == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Missing follower id"))
                    return@get
                }

                val followerId = followerIdParam.toIntOrNull()

                if (followerId == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid id"))
                    return@get
                }

                val artistIds = controller.getFollowedArtistsIds(followerId)
                val artistController = com.adapp.backend.Artist.Infrastructure.Controllers.KtorArtistController(artistRepo)
                val artists = artistIds.mapNotNull {
                    try {
                        artistController.getOneById(it)
                    } catch(e: Exception) {
                        null
                    }
                }
                call.respond(artists)
            } catch (e: Exception) {
                call.application.environment.log.error("Error getting followed artists", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("message" to (e.message ?: "Error getting followed artists")))
            }
        }

        // GET /follower/{followerId}/isFollowing/{artistId} - Verificar si un follower sigue a un artista
        get("/follower/{followerId}/isFollowing/{artistId}") {
            try {
                val followerIdParam = call.parameters["followerId"]
                val artistIdParam = call.parameters["artistId"]
                
                if (followerIdParam == null || artistIdParam == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Missing follower or artist id"))
                    return@get
                }
                
                val followerId = followerIdParam.toIntOrNull()
                val artistId = artistIdParam.toIntOrNull()
                
                if (followerId == null || artistId == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid id"))
                    return@get
                }

                val isFollowing = controller.isFollowing(followerId, artistId)
                call.respond(mapOf("isFollowing" to isFollowing))
            } catch (e: Exception) {
                call.application.environment.log.error("Error checking follow status", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("message" to (e.message ?: "Error checking follow status")))
            }
        }

        // GET /artist/{artistId}/followersCount - Obtener cantidad de seguidores de un artista
        get("/artist/{artistId}/followersCount") {
            try {
                val artistIdParam = call.parameters["artistId"]
                
                if (artistIdParam == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Missing artist id"))
                    return@get
                }
                
                val artistId = artistIdParam.toIntOrNull()
                
                if (artistId == null) {
                    call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid id"))
                    return@get
                }

                val count = controller.getFollowersCount(artistId)
                call.respond(mapOf("followersCount" to count))
            } catch (e: Exception) {
                call.application.environment.log.error("Error getting followers count", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("message" to (e.message ?: "Error getting followers count")))
            }
        }

        // GET /follower-artist - Obtener TODOS los registros de FollowerArtist (para debugging/testing)
        get("/follower-artist") {
            try {
                val allFollowerArtists = controller.getAllFollowerArtists()
                call.respond(HttpStatusCode.OK, allFollowerArtists)
            } catch (e: Exception) {
                call.application.environment.log.error("Error getting all follower-artist records", e)
                call.respond(HttpStatusCode.InternalServerError, mapOf("message" to (e.message ?: "Error getting records")))
            }
        }
    }
}

