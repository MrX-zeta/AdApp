package com.adapp.backend.FollowerArtist.Domain.Repositories

import com.adapp.backend.FollowerArtist.Domain.Models.FollowerArtist

interface FollowerArtistRepository {
    fun create(followerArtist: FollowerArtist)
    fun delete(followerId: Int, artistId: Int)
    fun getByFollowerId(followerId: Int): List<FollowerArtist>
    fun getByArtistId(artistId: Int): List<FollowerArtist>
    fun getAll(): List<FollowerArtist>
    fun exists(followerId: Int, artistId: Int): Boolean
}

