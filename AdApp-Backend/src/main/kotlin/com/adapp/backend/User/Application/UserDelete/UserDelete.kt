package com.adapp.backend.User.Application.UserDelete

import com.adapp.backend.User.Domain.Exceptions.UserNotFoundError
import com.adapp.backend.User.Domain.Models.UserId
import com.adapp.backend.User.Domain.Repositories.UserRepository

class UserDelete(private val userRepo : UserRepository) {
    suspend fun invoke(id: Int){
        val userId = UserId(id)

        val UserExists = userRepo.delete(userId)

        if(UserExists == null) throw UserNotFoundError("User Not Found")
    }
}