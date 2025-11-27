package com.adapp.backend

import com.adapp.backend.Event.Infrastructure.Routes.configureEventRouting
import com.adapp.backend.Follower.Infrastructure.Routes.configureFollowerRouting
import com.adapp.backend.Shared.Infrastructure.Plugins.configureSerialization
import com.adapp.backend.Song.Infrastructure.Routes.configureSongRouting
import com.adapp.backend.User.Infrastructure.Routes.configureRouting as configureUserRouting
import com.adapp.backend.Artist.Infrastructure.Routes.configureArtistRouting as configureArtistRouting
import com.adapp.backend.SocialMedia.Infrastructure.Routes.configureRouting as configureSocialMediaRouting
import com.adapp.backend.FollowerArtist.Infraestructure.Routes.configureFollowerArtistRouting
import com.adapp.examples.configureFrameworks

import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) {
    // Evitar que Netty use sun.misc.Unsafe en JDKs modernos (suprime warnings y llamadas a Unsafe)
    System.setProperty("io.netty.noUnsafe", "true")
    System.setProperty("io.netty.tryReflectionSetAccessible", "false")
    EngineMain.main(args)
}

fun Application.module() {
    // Configurar Koin (inyección de dependencias)
    configureFrameworks()

    // Configurar plugins
    configureSerialization()

    // Configurar rutas (los repositorios se inyectan automáticamente con Koin)
    configureUserRouting()
    configureArtistRouting()
    configureFollowerRouting()
    configureFollowerArtistRouting()
    configureEventRouting()
    configureSongRouting()
    configureSocialMediaRouting()
}
