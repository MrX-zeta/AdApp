package com.adapp.backend.Artist.Infrastructure.Repositories

import com.adapp.backend.Artist.Domain.Models.Artist
import com.adapp.backend.Artist.Domain.Repositories.ArtistRepository
import com.adapp.backend.User.Domain.Models.UserId

class InMemoryArtistRepository : ArtistRepository {
    private val artists = mutableListOf<Artist>()

    override fun create(artist: Artist) {
        artists.add(artist)
    }

    override fun edit(artist: Artist) {
        val index = artists.indexOfFirst { it.Usuarioid.value == artist.Usuarioid.value }
        if(index >= 0) artists[index] = artist
    }

    override fun getAllArtists(): List<Artist> = artists.toList()

    override fun getOneById(id: UserId): Artist? = artists.find { it.Usuarioid.value == id.value }

    override fun delete(id: UserId) {
        artists.removeAll { it.Usuarioid.value == id.value }
    }
}