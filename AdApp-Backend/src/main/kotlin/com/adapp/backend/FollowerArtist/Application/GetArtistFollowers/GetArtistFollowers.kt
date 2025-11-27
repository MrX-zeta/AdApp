package com.adapp.backend.FollowerArtist.Application.GetArtistFollowers

import com.adapp.backend.FollowerArtist.Domain.Models.FollowerArtist
import com.adapp.backend.FollowerArtist.Domain.Repositories.FollowerArtistRepository

class GetArtistFollowers(private val repository: FollowerArtistRepository) {
    operator fun invoke(artistId: Int): List<FollowerArtist> {
        return repository.getByArtistId(artistId)
    }
}

