package com.adapp.backend.Artist.Infrastructure.Controllers

import com.adapp.backend.Artist.Domain.Models.Artist
import com.adapp.backend.Artist.Domain.Models.ArtistContactNum
import com.adapp.backend.Artist.Domain.Models.ArtistFotoUrl
import com.adapp.backend.Artist.Domain.Models.ArtistDescription
import com.adapp.backend.Artist.Domain.Models.ArtistDTO
import com.adapp.backend.Artist.Domain.Repositories.ArtistRepository
import com.adapp.backend.User.Domain.Exceptions.UserNotFoundError
import com.adapp.backend.User.Domain.Models.UserId
import com.adapp.backend.User.Domain.Models.UserName
import com.adapp.backend.User.Domain.Models.UserEmail
import com.adapp.backend.User.Domain.Models.UserPsswd
import com.adapp.backend.User.Domain.Models.UserRol

class KtorArtistController(private val artistRepo: ArtistRepository) {


    fun getAll(): List<ArtistDTO> {
        return artistRepo.getAllArtists()
            .filter { artist -> artist.rol.value.equals("artist", ignoreCase = true) }
            .map { artist ->
                ArtistDTO(
                    id = artist.Usuarioid.value,
                    nombre = artist.nombre.value,
                    correo = artist.correo.value,
                    contrasena = artist.contrasena.value,
                    rol = artist.rol.value,
                    fotoUrl = artist.fotoUrlValue(),
                    contactNum = artist.contactNumValue(),
                    description = artist.descriptionValue()
                )
            }
    }

    fun getOneById(id: Int): ArtistDTO {
        val artist = artistRepo.getOneById(UserId(id)) ?: throw UserNotFoundError("Artist not found")
        return ArtistDTO(
            id = artist.Usuarioid.value,
            nombre = artist.nombre.value,
            correo = artist.correo.value,
            contrasena = artist.contrasena.value,
            rol = artist.rol.value,
            fotoUrl = artist.fotoUrlValue(),
            contactNum = artist.contactNumValue(),
            description = artist.descriptionValue()
        )
    }

    fun create(id: Int, name: String, email: String, passwd: String, rol: String, fotoUrl: String, contactNum: String, description: String = "") {
        val artist = Artist(
            UserId(id),
            UserName(name),
            UserEmail(email),
            UserPsswd(passwd),
            UserRol(rol),
            ArtistFotoUrl(fotoUrl),
            ArtistContactNum(contactNum),
            ArtistDescription(description)
        )
        artistRepo.create(artist)
    }

    /**
     * Crea SOLO el perfil de artista (tabla artists), asumiendo que el usuario ya existe en users
     */
    fun createArtistProfile(id: Int, fotoUrl: String, contactNum: String, description: String = "") {
        val artist = Artist(
            UserId(id),
            UserName(""),  // No se usa en la inserción de artists
            UserEmail(""), // No se usa en la inserción de artists
            UserPsswd(""), // No se usa en la inserción de artists
            UserRol("artist"), // No se usa en la inserción de artists
            ArtistFotoUrl(fotoUrl),
            ArtistContactNum(contactNum),
            ArtistDescription(description)
        )
        artistRepo.create(artist)
    }

    /**
     * Edita un artista. Si `newId` != `oldId` crea uno nuevo y borra el antiguo; lanza IllegalArgumentException si newId ya existe.
     */
    fun edit(oldId: Int, newId: Int, name: String, email: String, passwd: String, rol: String, fotoUrl: String, contactNum: String, description: String = "") {
        artistRepo.getOneById(UserId(oldId)) ?: throw UserNotFoundError("Artist not found")

        if (oldId != newId) {
            val conflict = artistRepo.getOneById(UserId(newId))
            if (conflict != null) throw IllegalArgumentException("Artist with id $newId already exists")

            val created = Artist(
                UserId(newId),
                UserName(name),
                UserEmail(email),
                UserPsswd(passwd),
                UserRol(rol),
                ArtistFotoUrl(fotoUrl),
                ArtistContactNum(contactNum),
                ArtistDescription(description)
            )
            artistRepo.create(created)
            artistRepo.delete(UserId(oldId))
            return
        }

        val updated = Artist(
            UserId(oldId),
            UserName(name),
            UserEmail(email),
            UserPsswd(passwd),
            UserRol(rol),
            ArtistFotoUrl(fotoUrl),
            ArtistContactNum(contactNum),
            ArtistDescription(description)
        )
        artistRepo.edit(updated)
    }

    fun delete(id: Int) {
        artistRepo.getOneById(UserId(id)) ?: throw UserNotFoundError("Artist not found")
        artistRepo.delete(UserId(id))
    }
}