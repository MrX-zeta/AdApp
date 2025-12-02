package com.adapp.backend.Event.Domain.Models

import com.adapp.backend.User.Domain.Models.UserId

class Event(
    val Event_Id: EventId,
    val ArtistId: UserId,
    val title: EventTitle,
    val description: EventDescription,
    val Event_Date: EventDate,
    val Status: EventStatus
) {
    fun mapToPrimitives(): Map<String, Any> {
        return mapOf(
            "id" to Event_Id.value,
            "artistId" to ArtistId.value,
            "title" to title.title,
            "description" to description.description,
            "Event_Date" to Event_Date.date.toString(),
            "Status" to Status.status
        )
    }
}