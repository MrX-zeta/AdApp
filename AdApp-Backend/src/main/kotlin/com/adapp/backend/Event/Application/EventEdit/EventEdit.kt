package com.adapp.backend.Event.Application.EventEdit

import com.adapp.backend.Event.Domain.Exceptions.EventNotFoundError
import com.adapp.backend.Event.Domain.Models.Event
import com.adapp.backend.Event.Domain.Models.EventDate
import com.adapp.backend.Event.Domain.Models.EventDescription
import com.adapp.backend.Event.Domain.Models.EventId
import com.adapp.backend.Event.Domain.Models.EventStatus
import com.adapp.backend.Event.Domain.Models.EventTitle
import com.adapp.backend.Event.Domain.Repositories.EventRepository
import com.adapp.backend.User.Domain.Models.UserId
import java.util.Date

class EventEdit(private val eventRepo: EventRepository) {
    suspend fun invoke(
        eventId: Int,
        artistId: Int,
        title: String,
        description: String,
        event_date: Date,
        status: String
    ){
        val eventExists = Event(
            EventId(eventId),
            UserId(artistId),
            EventTitle(title),
            EventDescription(description),
            EventDate(event_date),
            EventStatus(status)
        )

        if(eventExists == null) throw EventNotFoundError("Event Not Found")
    }
}