package com.adapp.backend.Shared.Infrastructure.Database

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.javatime.timestamp

object Tables {

    // Tabla: users
    object UsersTable : Table("users") {
        val id = integer("id").autoIncrement()
        val nombre = varchar("nombre", 100).nullable()
        val correo = varchar("correo", 150).uniqueIndex()
        val contrasena = varchar("contrasena", 255).nullable()
        val rol = varchar("rol", 20).nullable()

        override val primaryKey = PrimaryKey(id)
    }

    // Tabla: artists
    object ArtistsTable : Table("artists") {
        val id = integer("id").references(UsersTable.id, onDelete = ReferenceOption.CASCADE)
        val fotoUrl = varchar("foto_url", 500).nullable()
        val contactNum = varchar("contact_num", 20).nullable()
        val description = text("description").nullable()

        override val primaryKey = PrimaryKey(id)
    }

    // Tabla: followers
    object FollowersTable : Table("followers") {
        val id = integer("id").references(UsersTable.id, onDelete = ReferenceOption.CASCADE)

        override val primaryKey = PrimaryKey(id)
    }

    // Tabla: follower_artist (relación muchos a muchos)
    object FollowerArtistTable : Table("follower_artist") {
        val followerId = integer("follower_id").references(FollowersTable.id, onDelete = ReferenceOption.CASCADE)
        val artistId = integer("artist_id").references(ArtistsTable.id, onDelete = ReferenceOption.CASCADE)

        override val primaryKey = PrimaryKey(followerId, artistId)
    }

    // Tabla: social_media
    object SocialMediaTable : Table("social_media") {
        val id = integer("id").autoIncrement()
        val artistId = integer("artist_id").references(ArtistsTable.id, onDelete = ReferenceOption.CASCADE)
        val url = varchar("url", 500).nullable()

        override val primaryKey = PrimaryKey(id)
    }

    // Tabla: songs
    object SongsTable : Table("songs") {
        val id = integer("id").autoIncrement()
        val artistId = integer("artist_id").references(ArtistsTable.id, onDelete = ReferenceOption.CASCADE)
        val title = varchar("title", 200).nullable()
        val url = varchar("url", 500).nullable()
        val dateUploaded = long("date_upload").default(System.currentTimeMillis())

        override val primaryKey = PrimaryKey(id)
    }

    // Tabla: events
    object EventsTable : Table("events") {
        val id = integer("id").autoIncrement()
        val artistId = integer("artist_id").references(ArtistsTable.id, onDelete = ReferenceOption.CASCADE)
        val title = varchar("title", 200).nullable()
        val description = text("description").nullable()
        val eventDate = timestamp("event_date").nullable()
        val status = varchar("status", 20).default("active")

        override val primaryKey = PrimaryKey(id)
    }
}
