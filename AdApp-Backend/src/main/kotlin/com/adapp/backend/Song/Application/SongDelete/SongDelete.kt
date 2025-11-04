package com.adapp.backend.Song.Application.SongDelete

import com.adapp.backend.Song.Domain.Exceptions.SongNotFoundError
import com.adapp.backend.Song.Domain.Models.SongId
import com.adapp.backend.Song.Domain.Repositories.SongRepository

class SongDelete(private val songRepo: SongRepository) {
    suspend fun invoke(id: Int){
        val songId = SongId(id)
        val songExists = songRepo.delete(songId)

        if(songExists == null) throw SongNotFoundError("Song not found")
    }
}