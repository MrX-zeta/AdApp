package com.adapp.backend.Song.Domain.Repositories

import com.adapp.backend.Song.Domain.Models.Song
import com.adapp.backend.Song.Domain.Models.SongId

interface SongRepository {
    fun create(song: Song)
    fun edit(song: Song)
    fun getAllSongs(): List<Song>
    fun getOneById(id: SongId): Song?
    fun delete(id: SongId)
}