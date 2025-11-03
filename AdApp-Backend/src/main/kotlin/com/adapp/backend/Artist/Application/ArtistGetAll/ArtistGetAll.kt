package com.adapp.backend.Artist.Application.ArtistGetAll

import com.adapp.backend.Artist.Domain.Models.Artist
import com.adapp.backend.Artist.Domain.Repositories.ArtistRepository

class ArtistGetAll(private val artistRepo: ArtistRepository) {
    suspend fun invoke(): List<Artist>{
        return artistRepo.getAllUsers()
    }
}