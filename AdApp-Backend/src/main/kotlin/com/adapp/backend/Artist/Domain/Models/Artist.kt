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
    val fotoUrl: ArtistFotoUrl,
    val contactNum: ArtistContactNum,
    val description: ArtistDescription
): User(ArtistId, nombre, correo, contrasena, rol) {
    // Helper accessors to expose primitive values without relying on property access
    fun fotoUrlValue(): String = this.fotoUrl.value
    fun contactNumValue(): String = this.contactNum.value
    fun descriptionValue(): String = this.description.value

    // Backwards-compatible secondary constructor that accepts ArtistSocialMedia (ignored)
    constructor(
        ArtistId: UserId,
        nombre: UserName,
        correo: UserEmail,
        contrasena: UserPsswd,
        rol: UserRol,
        fotoUrl: ArtistFotoUrl,
        socialMedia: ArtistSocialMedia,
        contactNum: ArtistContactNum,
        artistDescription: ArtistDescription
    ) : this(ArtistId, nombre, correo, contrasena, rol, fotoUrl, contactNum, artistDescription)
}