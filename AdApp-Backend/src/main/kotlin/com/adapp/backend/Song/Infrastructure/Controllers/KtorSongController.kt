package com.adapp.backend.Song.Infrastructure.Controllers

import com.adapp.backend.Song.Domain.Models.Song
import com.adapp.backend.Song.Domain.Models.SongId
import com.adapp.backend.Song.Domain.Models.SongTitle
import com.adapp.backend.Song.Domain.Repositories.SongRepository
import com.adapp.backend.Song.Domain.Exceptions.SongNotFoundError
import com.adapp.backend.Song.Domain.Models.SongUrl
import com.adapp.backend.User.Domain.Models.UserId

class KtorSongController(private val songRepo: SongRepository) {
    fun getAll(): List<Map<String, Any>> {
        return songRepo.getAllSongs().map { it.mapToPrimitives() }
    }

    fun getOneById(id: Int): Map<String, Any> {
        val song = songRepo.getOneById(SongId(id)) ?: throw SongNotFoundError("Song not found")
        return song.mapToPrimitives()
    }

    fun create(id: Int, artistId: Int, title: String, url: String) {
        val song = Song(
            SongId(id),
            UserId(artistId),
            SongTitle(title),
            SongUrl(url)
        )
        songRepo.create(song)
    }

    fun edit(id: Int, artistId: Int, title: String, url: String) {
        val existing = songRepo.getOneById(SongId(id)) ?: throw SongNotFoundError("Song not found")
        val updated = Song(
            SongId(id),
            UserId(artistId),
            SongTitle(title),
            SongUrl(url)
        )
        songRepo.edit(updated)
    }

    fun delete(id: Int) {
        songRepo.getOneById(SongId(id)) ?: throw SongNotFoundError("Song not found")
        songRepo.delete(SongId(id))
    }
}