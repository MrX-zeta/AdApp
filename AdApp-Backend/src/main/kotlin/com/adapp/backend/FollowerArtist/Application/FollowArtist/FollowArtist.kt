package com.adapp.backend.FollowerArtist.Application.FollowArtist

import com.adapp.backend.FollowerArtist.Domain.Models.FollowerArtist
import com.adapp.backend.FollowerArtist.Domain.Repositories.FollowerArtistRepository

class FollowArtist(private val repository: FollowerArtistRepository) {
    operator fun invoke(followerId: Int, artistId: Int) {
        val followerArtist = FollowerArtist(followerId, artistId)
        repository.create(followerArtist)
    }
}

