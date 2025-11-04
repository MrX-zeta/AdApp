package com.adapp.backend.Event.Application.EventCreate

import com.adapp.backend.Event.Domain.Models.Event
import com.adapp.backend.Event.Domain.Models.EventDate
import com.adapp.backend.Event.Domain.Models.EventDescription
import com.adapp.backend.Event.Domain.Models.EventId
import com.adapp.backend.Event.Domain.Models.EventStatus
import com.adapp.backend.Event.Domain.Models.EventTitle
import com.adapp.backend.Event.Domain.Repositories.EventRepository
import com.adapp.backend.User.Domain.Models.UserId
import io.ktor.events.Events
import java.util.Date

class EventCreate(private val eventRepo: EventRepository) {
    suspend fun invoke(
        id: Int,
        artistId: Int,
        title: String,
        description: String,
        date: Date,
        status: String
    ){
        val event = Event(
            EventId(id),
            UserId(artistId),
            EventTitle(title),
            EventDescription(description),
            EventDate(date),
            EventStatus(status)
        )
        eventRepo.create(event)
    }
}