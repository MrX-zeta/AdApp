package com.adapp.backend.Event.Infrastructure.Controllers

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

class KtorEventController(private val eventRepo: EventRepository) {

    fun getAllEvents(): List<Map<String, Any>> {
        return eventRepo.getAllEvents().map { it.mapToPrimitives() }
    }

    fun getOneById(id: Int): Map<String, Any> {
        val event = eventRepo.getOneById(EventId(id)) ?: throw EventNotFoundError("Event not found")
        return event.mapToPrimitives()
    }

    fun create(id: Int, artistId: Int, title: String, eventDate: Date, description: String, status: String){
        val event = Event(
            EventId(id),
            UserId(artistId),
            EventTitle(title),
            EventDescription(description),
            EventDate(eventDate),
            EventStatus(status)
        )
        eventRepo.create(event)
    }

    fun edit(id: Int, artistId: Int, title: String, eventDate: Date, description: String, status: String){
        val existing = eventRepo.getOneById(EventId(id)) ?: throw EventNotFoundError("Event not found")
        val updated = Event(
            EventId(id),
            UserId(artistId),
            EventTitle(title),
            EventDescription(description),
            EventDate(eventDate),
            EventStatus(status)
        )
        eventRepo.edit(updated)
    }

    fun delete(id: Int){
        val existing = eventRepo.getOneById(EventId(id))?:throw EventNotFoundError("Event not found")
        eventRepo.delete(EventId(id))
    }
}