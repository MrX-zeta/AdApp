package com.adapp.backend.Follower.Application.FollowerGetAll

import com.adapp.backend.Follower.Domain.Models.Follower
import com.adapp.backend.Follower.Domain.Repsitories.FollowerRepository

class FollowerGetAll(private val fllwrRepo : FollowerRepository) {
    suspend fun invoke(): List<Follower> {
        return fllwrRepo.getAllFollowers()
    }
}