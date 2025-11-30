package com.adapp.backend.Artist.Infrastructure.Repositories

import com.adapp.backend.Artist.Domain.Models.Artist
import com.adapp.backend.Artist.Domain.Models.ArtistContactNum
import com.adapp.backend.Artist.Domain.Models.ArtistDescription
import com.adapp.backend.Artist.Domain.Models.ArtistFotoUrl
import com.adapp.backend.Artist.Domain.Repositories.ArtistRepository
import com.adapp.backend.Shared.Infrastructure.Database.Tables.ArtistsTable
import com.adapp.backend.Shared.Infrastructure.Database.Tables.UsersTable
import com.adapp.backend.User.Domain.Models.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class PostgresArtistRepository : ArtistRepository {

    override fun create(artist: Artist) {
        transaction {
            // Solo insertar en artists, asumiendo que el usuario ya existe en users
            ArtistsTable.insert {
                it[id] = artist.Usuarioid.value
                it[fotoUrl] = artist.fotoUrl.value
                it[contactNum] = artist.contactNum.value
                it[description] = artist.description.value
            }
        }
    }

    override fun edit(artist: Artist) {
        transaction {
            // 1. Actualizar users
            UsersTable.update({ UsersTable.id eq artist.Usuarioid.value }) {
                it[nombre] = artist.nombre.value
                it[correo] = artist.correo.value
                it[contrasena] = artist.contrasena.value
                it[rol] = artist.rol.value
            }

            // 2. Actualizar artists
            ArtistsTable.update({ ArtistsTable.id eq artist.Usuarioid.value }) {
                it[fotoUrl] = artist.fotoUrl.value
                it[contactNum] = artist.contactNum.value
                it[description] = artist.description.value
            }
        }
    }

    override fun getAllArtists(): List<Artist> {
        return transaction {
            (UsersTable innerJoin ArtistsTable)
                .selectAll()
                .where { UsersTable.rol eq "artist" }
                .map { rowToArtist(it) }
        }
    }

    override fun getOneById(id: UserId): Artist? {
        return transaction {
            (UsersTable innerJoin ArtistsTable)
                .selectAll()
                .where { UsersTable.id eq id.value }
                .map { rowToArtist(it) }
                .singleOrNull()
        }
    }

    override fun delete(id: UserId) {
        transaction {
            // Gracias a ON DELETE CASCADE, solo necesitamos eliminar de users
            UsersTable.deleteWhere(limit = null, op = {
                UsersTable.id eq id.value
            })
        }
    }

    private fun rowToArtist(row: ResultRow): Artist {
        return Artist(
            ArtistId = UserId(row[UsersTable.id]),
            nombre = UserName(row[UsersTable.nombre] ?: ""),
            correo = UserEmail(row[UsersTable.correo]),
            contrasena = UserPsswd(row[UsersTable.contrasena] ?: ""),
            rol = UserRol(row[UsersTable.rol] ?: "artist"),
            fotoUrl = ArtistFotoUrl(row[ArtistsTable.fotoUrl] ?: ""),
            contactNum = ArtistContactNum(row[ArtistsTable.contactNum] ?: ""),
            description = ArtistDescription(row[ArtistsTable.description] ?: "")
        )
    }
}

