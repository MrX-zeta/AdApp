package com.adapp.backend.User.Application.UserGetOneById

import com.adapp.backend.User.Domain.Exceptions.UserNotFoundError
import com.adapp.backend.User.Domain.Models.User
import com.adapp.backend.User.Domain.Models.UserId
import com.adapp.backend.User.Domain.Repositories.UserRepository

class UserGetOneById(private val userRepo: UserRepository) {
    suspend fun invoke(id: Int): User?{
        val user = userRepo.getOneById(UserId(id))

        if(user == null) throw UserNotFoundError("User not found")

        return userRepo.getOneById(UserId(id))
    }
}