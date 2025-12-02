package com.adapp.backend.Song.Infrastructure.Repositories

import com.adapp.backend.Song.Domain.Models.Song
import com.adapp.backend.Song.Domain.Models.SongId
import com.adapp.backend.Song.Domain.Repositories.SongRepository

class InMemorySongRepository: SongRepository {
    private val songs = mutableListOf<Song>()

    override fun create(song: Song): Song {
        songs.add(song)
        return song
    }

    override fun delete(id: SongId) {
        songs.removeAll { it.SongId.value == id.value }
    }

    override fun edit(song: Song) {
        val index = songs.indexOfFirst { it.SongId.value == song.SongId.value }
        if(index >= 0) {
            songs[index] = song
        }
    }

    override fun getAllSongs(): List<Song> = songs.toList()

    override fun getOneById(id: SongId): Song? = songs.find { it.SongId.value == id.value }
}