package com.adapp.backend.User.Domain.Models

import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    val id: Int,
    val nombre: String,
    val correo: String,
    val contrasena: String,
    val rol: String
)
