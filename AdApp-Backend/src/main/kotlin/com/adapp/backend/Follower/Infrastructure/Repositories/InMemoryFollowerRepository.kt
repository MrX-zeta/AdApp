package com.adapp.backend.Follower.Infrastructure.Repositories

import com.adapp.backend.Follower.Domain.Models.Follower
import com.adapp.backend.Follower.Domain.Repsitories.FollowerRepository
import com.adapp.backend.User.Domain.Models.UserId

class InMemoryFollowerRepository : FollowerRepository {
    private val followers = mutableListOf<Follower>()

    override fun create(follower: Follower) {
        followers.add(follower)
    }

    override fun edit(follower: Follower) {
        val index = followers.indexOfFirst { it.Usuarioid.value == follower.Usuarioid.value }
        if(index >= 0) followers[index] = follower
    }

    override fun getAllFollowers(): List<Follower> = followers.toList()

    override fun getOneById(id: UserId): Follower? = followers.find { it.Usuarioid.value == id.value }

    override fun delete(id: UserId) {
        followers.removeAll { it.Usuarioid.value == id.value }
    }
}