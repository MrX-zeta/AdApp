package com.adapp.backend.Shared.Infrastructure.Plugins

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import com.adapp.backend.Shared.Infrastructure.Database.Tables

fun Application.configureDatabases() {
    val config = environment.config

    try {
        // Conexión simple sin HikariCP
        Database.connect(
            url = config.property("postgres.url").getString(),
            driver = config.property("postgres.driver").getString(),
            user = config.property("postgres.user").getString(),
            password = config.property("postgres.password").getString()
        )

        log.info("Database connection established")

        // Crear tablas si no existen
        transaction {
            SchemaUtils.create(
                Tables.UsersTable,
                Tables.ArtistsTable,
                Tables.FollowersTable,
                Tables.FollowerArtistTable,
                Tables.SocialMediaTable,
                Tables.SongsTable,
                Tables.EventsTable
            )
        }

        log.info("Database tables created successfully")
    } catch (e: Exception) {
        log.error("Failed to configure database: ${e.message}", e)
        throw e
    }
}

