package com.adapp.backend.Follower.Infrastructure.Repositories

import com.adapp.backend.FollowerArtist.Domain.Models.FollowerArtist

class InMemoryFollowerArtistRepository {
    private val followedArtists = mutableListOf<FollowerArtist>()

    fun follow(followerId: Int, artistId: Int) {
        // Verificar si ya existe la relación
        val exists = followedArtists.any { 
            it.followerId == followerId && it.artistId == artistId 
        }
        
        if (!exists) {
            followedArtists.add(FollowerArtist(followerId, artistId))
        }
    }

    fun unfollow(followerId: Int, artistId: Int) {
        followedArtists.removeIf { 
            it.followerId == followerId && it.artistId == artistId 
        }
    }

    fun isFollowing(followerId: Int, artistId: Int): Boolean {
        return followedArtists.any { 
            it.followerId == followerId && it.artistId == artistId 
        }
    }

    fun getFollowedArtists(followerId: Int): List<Int> {
        return followedArtists
            .filter { it.followerId == followerId }
            .map { it.artistId }
    }

    fun getFollowersCount(artistId: Int): Int {
        return followedArtists.count { it.artistId == artistId }
    }

    fun getAllFollowers(artistId: Int): List<Int> {
        return followedArtists
            .filter { it.artistId == artistId }
            .map { it.followerId }
    }
}
