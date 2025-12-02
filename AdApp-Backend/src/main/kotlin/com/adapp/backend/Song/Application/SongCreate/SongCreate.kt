package com.adapp.backend.Song.Application.SongCreate

import com.adapp.backend.Song.Domain.Models.Song
import com.adapp.backend.Song.Domain.Models.SongId
import com.adapp.backend.Song.Domain.Models.SongTitle
import com.adapp.backend.Song.Domain.Models.SongUrl
import com.adapp.backend.Song.Domain.Repositories.SongRepository
import com.adapp.backend.User.Domain.Models.UserId

class SongCreate(private val songRepo: SongRepository) {
    suspend fun invoke(
        songId: Int,
        artistId: Int,
        title: String,
        url: String
    ){
        val song = Song(
            SongId(songId),
            UserId(artistId),
            SongTitle(title),
            SongUrl(url)
        )
        songRepo.create(song)
    }
}