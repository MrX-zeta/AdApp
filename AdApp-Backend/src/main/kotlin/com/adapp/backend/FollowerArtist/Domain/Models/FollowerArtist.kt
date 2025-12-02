package com.adapp.backend.FollowerArtist.Domain.Models

import kotlinx.serialization.Serializable

@Serializable
data class FollowerArtist(
    val followerId: Int,
    val artistId: Int
)

@Serializable
data class FollowerArtistDTO(
    val followerId: Int,
    val artistId: Int
)
