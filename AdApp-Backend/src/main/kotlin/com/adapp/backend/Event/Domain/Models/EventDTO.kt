package com.adapp.backend.Event.Domain.Models

import kotlinx.serialization.Serializable

@Serializable
data class EventDTO(
    val id: Int,
    val artistId: Int,
    val title: String,
    val description: String,
    val eventDate: String,
    val status: String
)
