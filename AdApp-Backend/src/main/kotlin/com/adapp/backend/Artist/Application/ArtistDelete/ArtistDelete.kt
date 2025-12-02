package com.adapp.backend.Artist.Application.ArtistDelete

import com.adapp.backend.Artist.Domain.Repositories.ArtistRepository
import com.adapp.backend.User.Domain.Exceptions.UserNotFoundError
import com.adapp.backend.User.Domain.Models.UserId

class ArtistDelete(private val artistRepo: ArtistRepository) {
    suspend fun invoke(id: Int){
        val artistId = UserId(id)

        val ArtistExists = artistRepo.delete(artistId)
        if(ArtistExists == null) throw UserNotFoundError("Artist Not Found")
    }
}