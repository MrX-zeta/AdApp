package com.adapp.backend.Song.Infrastructure.Repositories

import com.adapp.backend.Song.Domain.Models.*
import com.adapp.backend.Song.Domain.Repositories.SongRepository
import com.adapp.backend.Shared.Infrastructure.Database.Tables.SongsTable
import com.adapp.backend.User.Domain.Models.UserId
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class PostgresSongRepository : SongRepository {

    override fun create(song: Song): Song {
        return transaction {
            val insertedId = SongsTable.insert {
                it[artistId] = song.artistId.value
                it[title] = song.title.value
                it[url] = song.url.value
                it[dateUploaded] = song.dateUploaded
            }[SongsTable.id]
            
            Song(
                SongId = SongId(insertedId),
                artistId = song.artistId,
                title = song.title,
                url = song.url,
                dateUploaded = song.dateUploaded
            )
        }
    }

    override fun edit(song: Song) {
        transaction {
            SongsTable.update({ SongsTable.id eq song.SongId.value }) {
                it[artistId] = song.artistId.value
                it[title] = song.title.value
                it[url] = song.url.value
                it[dateUploaded] = song.dateUploaded
            }
        }
    }

    override fun delete(id: SongId) {
        transaction {
            SongsTable.deleteWhere(limit = null, op = {
                SongsTable.id eq id.value
            })
        }
    }

    override fun getAllSongs(): List<Song> {
        return transaction {
            SongsTable.selectAll()
                .map { rowToSong(it) }
        }
    }

    override fun getOneById(id: SongId): Song? {
        return transaction {
            SongsTable.selectAll()
                .where { SongsTable.id eq id.value }
                .map { rowToSong(it) }
                .singleOrNull()
        }
    }

    private fun rowToSong(row: ResultRow): Song {
        return Song(
            SongId = SongId(row[SongsTable.id]),
            artistId = UserId(row[SongsTable.artistId]),
            title = SongTitle(row[SongsTable.title] ?: ""),
            url = SongUrl(row[SongsTable.url] ?: ""),
            dateUploaded = row[SongsTable.dateUploaded]
        )
    }
}

