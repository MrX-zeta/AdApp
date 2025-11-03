package com.adapp.backend.User.Infrastructure.Controllers

import com.adapp.backend.User.Domain.Models.User
import com.adapp.backend.User.Domain.Models.UserId
import com.adapp.backend.User.Domain.Repositories.UserRepository
import com.adapp.backend.User.Domain.Exceptions.UserNotFoundError
import com.adapp.backend.User.Domain.Models.UserEmail
import com.adapp.backend.User.Domain.Models.UserName
import com.adapp.backend.User.Domain.Models.UserPsswd
import com.adapp.backend.User.Domain.Models.UserRol

class KtorUserController(private val userRepo: UserRepository) {
    fun getAll(): List<Map<String, Any>> {
        return userRepo.getAllUsers().map { it.mapToPrimitives() }
    }

    fun getOneById(id: Int): Map<String, Any> {
        val user = userRepo.getOneById(UserId(id)) ?: throw UserNotFoundError("User not found")
        return user.mapToPrimitives()
    }

    fun create(id: Int, name: String, email: String, passwd: String, rol: String) {
        // Convertir createdAt si es necesario; el dominio actual no usa createdAt en User
        val user = User(
            UserId(id),
            UserName(name),
            UserEmail(email),
            UserPsswd(passwd),
            UserRol(rol)
        )
        userRepo.create(user)
    }

    fun edit(id: Int, name: String, email: String, passwd: String, rol: String) {
        val existing = userRepo.getOneById(UserId(id)) ?: throw UserNotFoundError("User not found")
        val updated = User(
            existing.Usuarioid,
            UserName(name),
            UserEmail(email),
            UserPsswd(passwd),
            UserRol(rol)
        )
        userRepo.edit(updated)
    }

    fun delete(id: Int) {
        val existing = userRepo.getOneById(UserId(id)) ?: throw UserNotFoundError("User not found")
        userRepo.delete(UserId(id))
    }
}

