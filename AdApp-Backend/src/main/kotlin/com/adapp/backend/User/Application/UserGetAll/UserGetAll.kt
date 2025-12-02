package com.adapp.backend.User.Application.UserGetAll

import com.adapp.backend.User.Domain.Models.User
import com.adapp.backend.User.Domain.Repositories.UserRepository

class UserGetAll(private val userRepo: UserRepository) {
    suspend fun invoke() : List<User>{
        return userRepo.getAllUsers()
    }
}