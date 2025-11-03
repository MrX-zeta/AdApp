package com.adapp.backend.Follower.Application.FollowerGetOneById

import com.adapp.backend.Follower.Domain.Models.Follower
import com.adapp.backend.Follower.Domain.Repsitories.FollowerRepository
import com.adapp.backend.User.Domain.Exceptions.UserNotFoundError
import com.adapp.backend.User.Domain.Models.UserId

class FollowerGetOneById(private val fllwrRepo : FollowerRepository) {
    suspend fun invoke(id: Int): Follower? {
        val follower = fllwrRepo.getOneById(UserId(id))

        if(follower == null) throw UserNotFoundError("Follower not found")

        return follower
    }
}