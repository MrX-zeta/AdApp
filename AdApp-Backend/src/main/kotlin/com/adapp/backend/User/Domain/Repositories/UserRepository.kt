package com.adapp.backend.User.Domain.Repositories

import com.adapp.backend.User.Domain.Models.User
import com.adapp.backend.User.Domain.Models.UserId

interface UserRepository {
    fun create(user: User)
    fun edit(user: User)
    fun getAllUsers(): List<User>
    fun getOneById(id: UserId): User?
    fun delete(id: UserId)
}