package com.adapp.backend

import com.adapp.examples.configureDatabases
import com.adapp.examples.configureFrameworks
import com.adapp.examples.configureRouting
import com.adapp.examples.configureSecurity
import com.adapp.examples.configureSerialization
import com.adapp.backend.User.Infrastructure.Routes.configureRouting as configureUserRouting
import com.adapp.backend.Artist.Infrastructure.Routes.configureArtistRouting as configureArtistRouting

import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain

fun main(args: Array<String>) {
    // Evitar que Netty use sun.misc.Unsafe en JDKs modernos (suprime warnings y llamadas a Unsafe)
    System.setProperty("io.netty.noUnsafe", "true")
    System.setProperty("io.netty.tryReflectionSetAccessible", "false")
    EngineMain.main(args)
}

fun Application.module() {
    configureSecurity()
    configureFrameworks()
    configureSerialization()
    configureDatabases()
    configureRouting()
    configureUserRouting()
    configureArtistRouting()
}
