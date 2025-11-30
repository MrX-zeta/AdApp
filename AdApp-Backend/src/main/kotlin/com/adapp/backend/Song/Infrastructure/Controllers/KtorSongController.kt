package com.adapp.backend.Song.Infrastructure.Controllers

import com.adapp.backend.Follower.Domain.Models.FollowerDTO
import com.adapp.backend.Song.Domain.Models.Song
import com.adapp.backend.Song.Domain.Models.SongId
import com.adapp.backend.Song.Domain.Models.SongTitle
import com.adapp.backend.Song.Domain.Repositories.SongRepository
import com.adapp.backend.Song.Domain.Exceptions.SongNotFoundError
import com.adapp.backend.Song.Domain.Models.SongDTO
import com.adapp.backend.Song.Domain.Models.SongUrl
import com.adapp.backend.User.Domain.Models.UserId

class KtorSongController(private val songRepo: SongRepository) {
    fun getAll(): List<SongDTO> {
        return songRepo.getAllSongs().map { song ->
            SongDTO(
                id = song.SongId.value,
                artistId = song.artistId.value,
                title = song.title.value,
                url = song.url.value,
                dateUploaded = song.dateUploaded
            )
        }
    }

    fun getOneById(id: Int): SongDTO {
        val song = songRepo.getOneById(SongId(id)) ?: throw SongNotFoundError("Song not found")
        return SongDTO(
            id = song.SongId.value,
            artistId = song.artistId.value,
            title = song.title.value,
            url = song.url.value,
            dateUploaded = song.dateUploaded
        )
    }

    fun create(id: Int, artistId: Int, title: String, url: String, dateUploaded: Long = System.currentTimeMillis()) {
        val song = Song(
            SongId(id),
            UserId(artistId),
            SongTitle(title),
            SongUrl(url),
            dateUploaded
        )
        songRepo.create(song)
    }

    fun edit(id: Int, artistId: Int, title: String, url: String, dateUploaded: Long? = null) {
        val existing = songRepo.getOneById(SongId(id)) ?: throw SongNotFoundError("Song not found")
        val updated = Song(
            SongId(id),
            UserId(artistId),
            SongTitle(title),
            SongUrl(url),
            dateUploaded ?: existing.dateUploaded // Mantener fecha original si no se proporciona
        )
        songRepo.edit(updated)
    }

    fun delete(id: Int) {
        songRepo.getOneById(SongId(id)) ?: throw SongNotFoundError("Song not found")
        songRepo.delete(SongId(id))
    }
}