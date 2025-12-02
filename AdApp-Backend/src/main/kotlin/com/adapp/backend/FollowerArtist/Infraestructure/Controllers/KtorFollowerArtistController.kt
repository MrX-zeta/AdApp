package com.adapp.backend.FollowerArtist.Infraestructure.Controllers

import com.adapp.backend.FollowerArtist.Application.FollowArtist.FollowArtist
import com.adapp.backend.FollowerArtist.Application.UnfollowArtist.UnfollowArtist
import com.adapp.backend.FollowerArtist.Application.GetFollowedArtists.GetFollowedArtists
import com.adapp.backend.FollowerArtist.Application.GetArtistFollowers.GetArtistFollowers
import com.adapp.backend.FollowerArtist.Domain.Models.FollowerArtistDTO
import com.adapp.backend.FollowerArtist.Domain.Repositories.FollowerArtistRepository

class KtorFollowerArtistController(
    private val followArtistUseCase: FollowArtist,
    private val unfollowArtistUseCase: UnfollowArtist,
    private val getFollowedArtistsUseCase: GetFollowedArtists,
    private val getArtistFollowersUseCase: GetArtistFollowers,
    private val repository: FollowerArtistRepository
) {
    fun followArtist(followerId: Int, artistId: Int) {
        followArtistUseCase.invoke(followerId, artistId)
    }

    fun unfollowArtist(followerId: Int, artistId: Int) {
        unfollowArtistUseCase.invoke(followerId, artistId)
    }

    fun getFollowedArtists(followerId: Int): List<FollowerArtistDTO> {
        return getFollowedArtistsUseCase.invoke(followerId).map {
            FollowerArtistDTO(it.followerId, it.artistId)
        }
    }

    fun getArtistFollowers(artistId: Int): List<FollowerArtistDTO> {
        return getArtistFollowersUseCase.invoke(artistId).map {
            FollowerArtistDTO(it.followerId, it.artistId)
        }
    }

    fun isFollowing(followerId: Int, artistId: Int): Boolean {
        return repository.exists(followerId, artistId)
    }

    fun getFollowersCount(artistId: Int): Int {
        return getArtistFollowersUseCase.invoke(artistId).size
    }

    fun getFollowedArtistsIds(followerId: Int): List<Int> {
        return getFollowedArtistsUseCase.invoke(followerId).map { it.artistId }
    }

    fun getArtistFollowersIds(artistId: Int): List<Int> {
        return getArtistFollowersUseCase.invoke(artistId).map { it.followerId }
    }

    fun getAllFollowerArtists(): List<FollowerArtistDTO> {
        return repository.getAll().map {
            FollowerArtistDTO(it.followerId, it.artistId)
        }
    }
}
