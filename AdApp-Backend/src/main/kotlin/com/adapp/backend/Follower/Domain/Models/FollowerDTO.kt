package com.adapp.backend.Follower.Domain.Models

import kotlinx.serialization.Serializable

@Serializable
data class FollowerDTO(
    val id: Int,
    val nombre: String,
    val correo: String,
    val contrasena: String,
    val rol: String
)
