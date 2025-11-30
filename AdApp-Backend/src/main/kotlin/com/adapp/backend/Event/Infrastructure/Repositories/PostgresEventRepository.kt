package com.adapp.backend.Event.Infrastructure.Repositories

import com.adapp.backend.Event.Domain.Models.*
import com.adapp.backend.Event.Domain.Repositories.EventRepository
import com.adapp.backend.Shared.Infrastructure.Database.Tables.EventsTable
import com.adapp.backend.User.Domain.Models.UserId
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.Instant
import java.util.*

class PostgresEventRepository : EventRepository {

    override fun create(event: Event) {
        transaction {
            EventsTable.insert {
                it[artistId] = event.ArtistId.value
                it[title] = event.title.title
                it[description] = event.description.description
                it[eventDate] = event.Event_Date.date.toInstant()
                it[status] = event.Status.status
            }
        }
    }

    override fun edit(event: Event) {
        transaction {
            EventsTable.update({ EventsTable.id eq event.Event_Id.value }) {
                it[artistId] = event.ArtistId.value
                it[title] = event.title.title
                it[description] = event.description.description
                it[eventDate] = event.Event_Date.date.toInstant()
                it[status] = event.Status.status
            }
        }
    }

    override fun delete(id: EventId) {
        transaction {
            EventsTable.deleteWhere(limit = null, op = {
                EventsTable.id eq id.value
            })
        }
    }

    override fun getAllEvents(): List<Event> {
        return transaction {
            EventsTable.selectAll()
                .map { rowToEvent(it) }
        }
    }

    override fun getOneById(id: EventId): Event? {
        return transaction {
            EventsTable.selectAll()
                .where { EventsTable.id eq id.value }
                .map { rowToEvent(it) }
                .singleOrNull()
        }
    }

    private fun rowToEvent(row: ResultRow): Event {
        val instant: Instant? = row[EventsTable.eventDate]
        val date = if (instant != null) {
            Date.from(instant)
        } else {
            Date() // Fecha por defecto si es null
        }

        return Event(
            Event_Id = EventId(row[EventsTable.id]),
            ArtistId = UserId(row[EventsTable.artistId]),
            title = EventTitle(row[EventsTable.title] ?: ""),
            description = EventDescription(row[EventsTable.description] ?: ""),
            Event_Date = EventDate(date),
            Status = EventStatus(row[EventsTable.status])
        )
    }
}