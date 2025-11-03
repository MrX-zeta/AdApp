package com.adapp.backend.Artist.Application.ArtistEdit

import com.adapp.backend.Artist.Domain.Models.Artist
import com.adapp.backend.Artist.Domain.Models.ArtistContactNum
import com.adapp.backend.Artist.Domain.Models.ArtistFotoUrl
import com.adapp.backend.Artist.Domain.Models.ArtistSocialMedia
import com.adapp.backend.Artist.Domain.Repositories.ArtistRepository
import com.adapp.backend.User.Domain.Exceptions.UserNotFoundError
import com.adapp.backend.User.Domain.Models.UserEmail
import com.adapp.backend.User.Domain.Models.UserId
import com.adapp.backend.User.Domain.Models.UserName
import com.adapp.backend.User.Domain.Models.UserPsswd
import com.adapp.backend.User.Domain.Models.UserRol

class ArtistEdit(private val artistRepo: ArtistRepository) {
    suspend fun invoke(
        id: Int,
        name: String,
        correo: String,
        contrasena: String,
        rol: String,
        fotoUrl: String,
        redesSociales: String,
        contactNum: String
    ){
        val artist = Artist(
            UserId(id),
            UserName(name),
            UserEmail(correo),
            UserPsswd(contrasena),
            UserRol(rol),
            ArtistFotoUrl(fotoUrl),
            ArtistSocialMedia(redesSociales),
            ArtistContactNum(contactNum)
        )

        val artistExists = artistRepo.edit(artist)

        if(artistExists == null) throw UserNotFoundError("Artist Not Found")
    }
}