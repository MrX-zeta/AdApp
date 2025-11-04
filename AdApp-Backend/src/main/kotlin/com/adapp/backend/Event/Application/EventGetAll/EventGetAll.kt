package com.adapp.backend.Event.Application.EventGetAll

import com.adapp.backend.Event.Domain.Models.Event
import com.adapp.backend.Event.Domain.Repositories.EventRepository

class EventGetAll(private val eventRepo: EventRepository) {
    suspend fun invoke(): List<Event>{
        return eventRepo.getAllEvents()
    }
}