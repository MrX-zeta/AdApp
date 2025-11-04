package com.adapp.backend.Song.Application.SongGetAll

import com.adapp.backend.Song.Domain.Models.Song
import com.adapp.backend.Song.Domain.Repositories.SongRepository

class SongGetAllSongs(private val songRepo: SongRepository) {
    suspend fun invoke(): List<Song> {
        return songRepo.getAllSongs()
    }
}