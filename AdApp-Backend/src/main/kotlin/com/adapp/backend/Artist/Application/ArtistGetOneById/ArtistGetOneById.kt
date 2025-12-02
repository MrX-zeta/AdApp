package com.adapp.backend.Artist.Application.ArtistGetOneById

import com.adapp.backend.Artist.Domain.Models.Artist
import com.adapp.backend.Artist.Domain.Repositories.ArtistRepository
import com.adapp.backend.User.Domain.Exceptions.UserNotFoundError
import com.adapp.backend.User.Domain.Models.UserId

class ArtistGetOneById(private val artistRepo: ArtistRepository) {
    suspend fun invoke(id: Int): Artist?{
        val artist = artistRepo.getOneById(UserId(id))

        if(artist == null) throw UserNotFoundError("Artist not found")

        return artist
    }
}