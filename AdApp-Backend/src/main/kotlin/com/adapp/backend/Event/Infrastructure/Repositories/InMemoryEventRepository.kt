package com.adapp.backend.Event.Infrastructure.Repositories

import com.adapp.backend.Event.Domain.Models.Event
import com.adapp.backend.Event.Domain.Models.EventId
import com.adapp.backend.Event.Domain.Repositories.EventRepository

class InMemoryEventRepository: EventRepository {
    private val events = mutableListOf<Event>()

    override fun create(event: Event) {
        events.add(event)
    }

    override fun delete(id: EventId) {
        events.removeAll { it.Event_Id.value == id.value }
    }

    override fun edit(event: Event) {
        val index = events.indexOfFirst { it.Event_Id.value == event.Event_Id.value }
        if (index >= 0) {
            events[index] = event
        }
    }

    override fun getAllEvents(): List<Event> = events.toList()

    override fun getOneById(id: EventId): Event? = events.find { it.Event_Id.value == id.value }
}