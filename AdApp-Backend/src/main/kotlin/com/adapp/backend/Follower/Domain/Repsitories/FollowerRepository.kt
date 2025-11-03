package com.adapp.backend.Follower.Domain.Repsitories

import com.adapp.backend.Follower.Domain.Models.Follower
import com.adapp.backend.User.Domain.Models.UserId

interface FollowerRepository {
    fun create(follower: Follower)
    fun edit(follower: Follower)
    fun getAllFollowers(): List<Follower>
    fun getOneById(id: UserId): Follower?
    fun delete(id: UserId)
}