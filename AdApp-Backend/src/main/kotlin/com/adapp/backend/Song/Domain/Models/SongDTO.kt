package com.adapp.backend.Song.Domain.Models

import kotlinx.serialization.Serializable

@Serializable
data class SongDTO(
    val id: Int,
    val artistId: Int,
    val title: String,
    val url: String
)
