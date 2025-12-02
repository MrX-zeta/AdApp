package com.adapp.backend.FollowerArtist.Application.UnfollowArtist

import com.adapp.backend.FollowerArtist.Domain.Repositories.FollowerArtistRepository

class UnfollowArtist(private val repository: FollowerArtistRepository) {
    operator fun invoke(followerId: Int, artistId: Int) {
        repository.delete(followerId, artistId)
    }
}

