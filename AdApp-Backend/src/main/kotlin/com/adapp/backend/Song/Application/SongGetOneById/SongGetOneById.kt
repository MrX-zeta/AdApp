package com.adapp.backend.Song.Application.SongGetOneById

import com.adapp.backend.Song.Domain.Exceptions.SongNotFoundError
import com.adapp.backend.Song.Domain.Models.Song
import com.adapp.backend.Song.Domain.Models.SongId
import com.adapp.backend.Song.Domain.Repositories.SongRepository

class SongGetOneById(private val songRepo: SongRepository) {
    suspend fun invoke(id: Int): Song?{
        val song = songRepo.getOneById(SongId(id))
        if(song == null) throw SongNotFoundError("Song not found")

        return song
    }
}