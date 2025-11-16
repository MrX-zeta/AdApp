package com.adapp.backend.Event.Domain.Models

import com.adapp.backend.User.Domain.Models.UserId
import kotlinx.serialization.Serializable

@Serializable
data class EventDTO(
    val id: Int,
    val artistId: Int,
    val title: String,
    val description: String,
    val eventDate: Long,
    val status: String
)
