package com.adapp.backend.Event.Domain.Repositories

import com.adapp.backend.Event.Domain.Models.Event
import com.adapp.backend.Event.Domain.Models.EventId

interface EventRepository {
    fun create(event: Event)
    fun edit(event: Event)
    fun delete(id: EventId)
    fun getAllEvents(): List<Event>
    fun getOneById(id: EventId): Event?
}