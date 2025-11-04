package com.adapp.backend.Event.Application.EventGetOneById

import com.adapp.backend.Event.Domain.Exceptions.EventNotFoundError
import com.adapp.backend.Event.Domain.Models.Event
import com.adapp.backend.Event.Domain.Models.EventId
import com.adapp.backend.Event.Domain.Repositories.EventRepository

class EventGetOneById(private val eventRepo: EventRepository) {
    suspend fun invoke(id: Int): Event?{
        val event = eventRepo.getOneById(EventId(id))

        if(event == null) throw EventNotFoundError("Event not found")
        return event
    }
}