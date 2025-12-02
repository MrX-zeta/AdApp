package com.adapp.backend.User.Infrastructure.Controllers

import com.adapp.backend.User.Domain.Models.User
import com.adapp.backend.User.Domain.Models.UserId
import com.adapp.backend.User.Domain.Models.UserDTO
import com.adapp.backend.User.Domain.Repositories.UserRepository
import com.adapp.backend.User.Domain.Exceptions.UserNotFoundError
import com.adapp.backend.User.Domain.Models.UserEmail
import com.adapp.backend.User.Domain.Models.UserName
import com.adapp.backend.User.Domain.Models.UserPsswd
import com.adapp.backend.User.Domain.Models.UserRol

class KtorUserController(private val userRepo: UserRepository) {

    fun getAll(): List<UserDTO> {
        return userRepo.getAllUsers().map { user ->
            UserDTO(
                id = user.Usuarioid.value,
                nombre = user.nombre.value,
                correo = user.correo.value,
                contrasena = user.contrasena.value,
                rol = user.rol.value
            )
        }
    }

    fun getOneById(id: Int): UserDTO {
        val user = userRepo.getOneById(UserId(id)) ?: throw UserNotFoundError("User not found")
        return UserDTO(
            id = user.Usuarioid.value,
            nombre = user.nombre.value,
            correo = user.correo.value,
            contrasena = user.contrasena.value,
            rol = user.rol.value
        )
    }

    fun create(id: Int, name: String, email: String, passwd: String, rol: String) {
        val user = User(
            UserId(id),
            UserName(name),
            UserEmail(email),
            UserPsswd(passwd),
            UserRol(rol)
        )
        userRepo.create(user)
    }

    /**
     * Edita un usuario. Si `newId` != `oldId` crea uno nuevo y borra el antiguo; lanza IllegalArgumentException si newId ya existe.
     */
    fun edit(oldId: Int, newId: Int, name: String, email: String, passwd: String, rol: String) {
        userRepo.getOneById(UserId(oldId)) ?: throw UserNotFoundError("User not found")

        if (oldId != newId) {
            val conflict = userRepo.getOneById(UserId(newId))
            if (conflict != null) throw IllegalArgumentException("User with id $newId already exists")

            val created = User(
                UserId(newId),
                UserName(name),
                UserEmail(email),
                UserPsswd(passwd),
                UserRol(rol)
            )
            userRepo.create(created)
            userRepo.delete(UserId(oldId))
            return
        }

        val updated = User(
            UserId(oldId),
            UserName(name),
            UserEmail(email),
            UserPsswd(passwd),
            UserRol(rol)
        )
        userRepo.edit(updated)
    }

    fun delete(id: Int) {
        userRepo.getOneById(UserId(id)) ?: throw UserNotFoundError("User not found")
        userRepo.delete(UserId(id))
    }
}
