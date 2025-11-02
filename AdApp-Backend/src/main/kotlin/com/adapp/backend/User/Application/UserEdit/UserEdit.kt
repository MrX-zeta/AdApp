package com.adapp.backend.User.Application.UserEdit

import com.adapp.backend.User.Domain.Exceptions.UserNotFoundError
import com.adapp.backend.User.Domain.Models.User
import com.adapp.backend.User.Domain.Models.UserEmail
import com.adapp.backend.User.Domain.Models.UserId
import com.adapp.backend.User.Domain.Models.UserName
import com.adapp.backend.User.Domain.Models.UserPsswd
import com.adapp.backend.User.Domain.Models.UserRol
import com.adapp.backend.User.Domain.Repositories.UserRepository

class UserEdit(private val userRepo: UserRepository) {
    suspend fun invoke(
        id: Int,
        name: String,
        email: String,
        password: String,
        role: String
    ){
        val user = User(
            UserId(id),
            UserName(name),
            UserEmail(email),
            UserPsswd(password),
            UserRol(role)
        )

        val userExists = userRepo.edit(user)

        if(userExists == null) throw UserNotFoundError("User Not Found")
    }
}