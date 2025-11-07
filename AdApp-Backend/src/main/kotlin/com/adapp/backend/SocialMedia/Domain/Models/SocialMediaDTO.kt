package com.adapp.backend.SocialMedia.Domain.Models

import kotlinx.serialization.Serializable

@Serializable
data class SocialMediaDTO(
    val id: Int,
    val artistId: Int,
    val url: String
)