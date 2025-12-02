package com.adapp.backend.FollowerArtist.Application.GetFollowedArtists

import com.adapp.backend.FollowerArtist.Domain.Models.FollowerArtist
import com.adapp.backend.FollowerArtist.Domain.Repositories.FollowerArtistRepository

class GetFollowedArtists(private val repository: FollowerArtistRepository) {
    operator fun invoke(followerId: Int): List<FollowerArtist> {
        return repository.getByFollowerId(followerId)
    }
}

