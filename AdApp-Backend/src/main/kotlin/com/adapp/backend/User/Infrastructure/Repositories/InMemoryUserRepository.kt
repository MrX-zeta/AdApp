package com.adapp.backend.User.Infrastructure.Repositories

import com.adapp.backend.User.Domain.Models.User
import com.adapp.backend.User.Domain.Models.UserId
import com.adapp.backend.User.Domain.Repositories.UserRepository

class InMemoryUserRepository : UserRepository {
    private val users = mutableListOf<User>()

    override fun create(user: User) {
        users.add(user)
    }

    override fun edit(user: User) {
        val index = users.indexOfFirst { it.Usuarioid.value == user.Usuarioid.value }
        if (index >= 0) {
            users[index] = user
        }
    }

    override fun getAllUsers(): List<User> = users.toList()

    override fun getOneById(id: UserId): User? = users.find { it.Usuarioid.value == id.value }

    override fun delete(id: UserId) {
        users.removeAll { it.Usuarioid.value == id.value }
    }
}