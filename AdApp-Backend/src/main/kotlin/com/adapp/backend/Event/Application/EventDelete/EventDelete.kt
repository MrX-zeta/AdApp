package com.adapp.backend.Event.Application.EventDelete

import com.adapp.backend.Event.Domain.Exceptions.EventNotFoundError
import com.adapp.backend.Event.Domain.Models.EventId
import com.adapp.backend.Event.Domain.Repositories.EventRepository

class EventDelete(private val eventRepo: EventRepository) {
    suspend fun invoke(id:Int){
        val eventId = EventId(id)

        val eventExists = eventRepo.delete(eventId)
        if(eventExists == null) throw EventNotFoundError("Event Not Found")
    }
}