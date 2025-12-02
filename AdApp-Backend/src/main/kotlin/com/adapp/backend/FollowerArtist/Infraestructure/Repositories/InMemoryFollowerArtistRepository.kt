package com.adapp.backend.FollowerArtist.Infraestructure.Repositories

import com.adapp.backend.FollowerArtist.Domain.Models.FollowerArtist
import com.adapp.backend.FollowerArtist.Domain.Repositories.FollowerArtistRepository

class InMemoryFollowerArtistRepository : FollowerArtistRepository {
    private val followers = mutableListOf<FollowerArtist>()

    override fun create(followerArtist: FollowerArtist) {
        if (!exists(followerArtist.followerId, followerArtist.artistId)) {
            followers.add(followerArtist)
        }
    }

    override fun delete(followerId: Int, artistId: Int) {
        followers.removeAll { it.followerId == followerId && it.artistId == artistId }
    }

    override fun getByFollowerId(followerId: Int): List<FollowerArtist> {
        return followers.filter { it.followerId == followerId }
    }

    override fun getByArtistId(artistId: Int): List<FollowerArtist> {
        return followers.filter { it.artistId == artistId }
    }

    override fun getAll(): List<FollowerArtist> {
        return followers.toList()
    }

    override fun exists(followerId: Int, artistId: Int): Boolean {
        return followers.any { it.followerId == followerId && it.artistId == artistId }
    }
}

