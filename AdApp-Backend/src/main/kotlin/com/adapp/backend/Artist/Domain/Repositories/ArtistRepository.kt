package com.adapp.backend.Artist.Domain.Repositories

import com.adapp.backend.Artist.Domain.Models.Artist
import com.adapp.backend.User.Domain.Models.User
import com.adapp.backend.User.Domain.Models.UserId

interface ArtistRepository {
    fun create(artist: Artist)
    fun edit(artist: Artist)
    fun getAllUsers(): List<Artist>
    fun getOneById(id: UserId): Artist?
    fun delete(id: UserId)
}