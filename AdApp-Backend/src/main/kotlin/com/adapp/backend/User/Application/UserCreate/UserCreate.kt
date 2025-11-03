package com.adapp.backend.User.Application.UserCreate

import com.adapp.backend.User.Domain.Models.User
import com.adapp.backend.User.Domain.Models.UserEmail
import com.adapp.backend.User.Domain.Models.UserId
import com.adapp.backend.User.Domain.Models.UserName
import com.adapp.backend.User.Domain.Models.UserPsswd
import com.adapp.backend.User.Domain.Models.UserRol
import com.adapp.backend.User.Domain.Repositories.UserRepository

class UserCreate(private val userRepo : UserRepository) {
    suspend fun invoke(
        userId: Int,
        name: String,
        password: String,
        email: String,
        role: String
    ){
        val user = User(
            UserId(userId),
            UserName(name),
            UserEmail(email),
            UserPsswd(password),
            UserRol(role)
        )
        userRepo.create(user)
    }
}