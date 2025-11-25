package com.adapp.backend

import com.adapp.backend.Event.Infrastructure.Routes.configureEventRouting
import com.adapp.backend.Follower.Infrastructure.Routes.configureFollowerRouting
import com.adapp.backend.Shared.Infrastructure.Plugins.configureSerialization
import com.adapp.backend.Song.Infrastructure.Routes.configureSongRouting
import com.adapp.backend.User.Infrastructure.Routes.configureRouting as configureUserRouting
import com.adapp.backend.Artist.Infrastructure.Routes.configureArtistRouting as configureArtistRouting
import com.adapp.backend.SocialMedia.Infrastructure.Routes.configureRouting as configureSocialMediaRouting
import com.adapp.backend.User.Infrastructure.Repositories.InMemoryUserRepository
import com.adapp.backend.Artist.Infrastructure.Repositories.InMemoryArtistRepository
import com.adapp.backend.SocialMedia.Infrastructure.Repositories.InMemorySocialMediaRepository

import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) {
    // Evitar que Netty use sun.misc.Unsafe en JDKs modernos (suprime warnings y llamadas a Unsafe)
    System.setProperty("io.netty.noUnsafe", "true")
    System.setProperty("io.netty.tryReflectionSetAccessible", "false")
    EngineMain.main(args)
}

fun Application.module() {
    // Crear repositorios compartidos una sola vez para toda la aplicación
    val userRepo = InMemoryUserRepository()
    val artistRepo = InMemoryArtistRepository()
    val socialMediaRepo = InMemorySocialMediaRepository()

    configureSerialization()
    configureUserRouting(userRepo, artistRepo)
    configureArtistRouting(artistRepo, socialMediaRepo)
    configureFollowerRouting()
    configureEventRouting()
    configureSongRouting()
    configureSocialMediaRouting(socialMediaRepo)
}
