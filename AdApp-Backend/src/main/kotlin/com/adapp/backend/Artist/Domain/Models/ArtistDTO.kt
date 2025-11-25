package com.adapp.backend.Artist.Domain.Models

import kotlinx.serialization.Serializable

@Serializable
data class ArtistDTO(
    val id: Int,
    val nombre: String,
    val correo: String,
    val contrasena: String,
    val rol: String,
    val fotoUrl: String,
    val contactNum: String,
    val description: String
)
