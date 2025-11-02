package com.adapp.backend.Artist.Domain.Models

import com.adapp.backend.User.Domain.Models.User
import com.adapp.backend.User.Domain.Models.UserEmail
import com.adapp.backend.User.Domain.Models.UserId
import com.adapp.backend.User.Domain.Models.UserName
import com.adapp.backend.User.Domain.Models.UserPsswd
import com.adapp.backend.User.Domain.Models.UserRol

class Artist(
    ArtistId: UserId,
    nombre: UserName,
    correo: UserEmail,
    contrasena: UserPsswd,
    rol: UserRol,
    fotoUrl: ArtistFotoUrl,
    SocialMedia: ArtistSocialMedia,
    ContactNum: ArtistContactNum
): User(ArtistId, nombre, correo, contrasena, rol) {
    //especific methods here
}