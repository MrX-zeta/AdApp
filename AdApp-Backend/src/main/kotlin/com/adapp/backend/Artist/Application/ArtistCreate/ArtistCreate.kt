package com.adapp.backend.Artist.Application.ArtistCreate

import com.adapp.backend.Artist.Domain.Models.Artist
import com.adapp.backend.Artist.Domain.Models.ArtistContactNum
import com.adapp.backend.Artist.Domain.Models.ArtistFotoUrl
import com.adapp.backend.User.Domain.Models.UserEmail
import com.adapp.backend.User.Domain.Models.UserId
import com.adapp.backend.User.Domain.Models.UserName
import com.adapp.backend.User.Domain.Models.UserPsswd
import com.adapp.backend.User.Domain.Models.UserRol
import com.adapp.backend.User.Domain.Repositories.UserRepository

class ArtistCreate(private val artistRepo: UserRepository) {
    suspend fun invoke(
        artistId: Int,
        nombre: String,
        correo: String,
        contrasena: String,
        rol: String,
        fotoUrl: String,
        NumContacto: String
    ){
        val artist = Artist(
            UserId(artistId),
            UserName(nombre),
            UserEmail(correo),
            UserPsswd(contrasena),
            UserRol(rol),
            ArtistFotoUrl(fotoUrl),
            ArtistContactNum(NumContacto)
        )
        artistRepo.create(artist)
    }
}