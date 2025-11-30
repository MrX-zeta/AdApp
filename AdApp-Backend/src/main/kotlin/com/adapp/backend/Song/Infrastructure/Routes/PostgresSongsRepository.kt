package com.adapp.backend.Song.Infrastructure.Routes

import com.adapp.backend.Song.Domain.Models.*
import com.adapp.backend.Song.Domain.Repositories.SongRepository
import com.adapp.backend.Shared.Infrastructure.Database.Tables.SongsTable
import com.adapp.backend.User.Domain.Models.UserId
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class PostgresSongsRepository : SongRepository {

    override fun create(song: Song) {
        transaction {
            SongsTable.insert {
                it[artistId] = song.artistId.value
                it[title] = song.title.value
                it[url] = song.url.value
            }
        }
    }

    override fun edit(song: Song) {
        transaction {
            SongsTable.update({ SongsTable.id eq song.SongId.value }) {
                it[artistId] = song.artistId.value
                it[title] = song.title.value
                it[url] = song.url.value
            }
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

    override fun delete(id: SongId) {
        transaction {
            SongsTable.deleteWhere {
                SongsTable.id eq id.value
            }
        }
    }

    private fun rowToSong(row: ResultRow): Song {
        return Song(
            SongId = SongId(row[SongsTable.id]),
            artistId = UserId(row[SongsTable.artistId]),
            title = SongTitle(row[SongsTable.title] ?: ""),
            url = SongUrl(row[SongsTable.url] ?: "")
        )
    }
}