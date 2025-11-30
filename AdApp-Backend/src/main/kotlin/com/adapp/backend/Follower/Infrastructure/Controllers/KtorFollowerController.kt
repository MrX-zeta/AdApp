package com.adapp.backend.Follower.Infrastructure.Controllers

import com.adapp.backend.Follower.Domain.Models.Follower
import com.adapp.backend.Follower.Domain.Models.FollowerDTO
import com.adapp.backend.Follower.Domain.Repsitories.FollowerRepository
import com.adapp.backend.User.Domain.Exceptions.UserNotFoundError
import com.adapp.backend.User.Domain.Models.UserId
import com.adapp.backend.User.Domain.Models.UserName
import com.adapp.backend.User.Domain.Models.UserEmail
import com.adapp.backend.User.Domain.Models.UserPsswd
import com.adapp.backend.User.Domain.Models.UserRol

class KtorFollowerController(private val followerRepo: FollowerRepository) {

    fun getAll(): List<FollowerDTO> {
        return followerRepo.getAllFollowers().map { follower ->
            FollowerDTO(
                id = follower.Usuarioid.value,
                nombre = follower.nombre.value,
                correo = follower.correo.value,
                contrasena = follower.contrasena.value,
                rol = follower.rol.value
            )
        }
    }

    fun getOneById(id: Int): FollowerDTO {
        val follower = followerRepo.getOneById(UserId(id)) ?: throw UserNotFoundError("Follower not found")
        return FollowerDTO(
            id = follower.Usuarioid.value,
            nombre = follower.nombre.value,
            correo = follower.correo.value,
            contrasena = follower.contrasena.value,
            rol = follower.rol.value
        )
    }

    fun create(id: Int, name: String, email: String, passwd: String, rol: String) {
        val follower = Follower(
            UserId(id),
            UserName(name),
            UserEmail(email),
            UserPsswd(passwd),
            UserRol(rol)
        )
        followerRepo.create(follower)
    }

    /**
     * Crea SOLO el perfil de follower (tabla followers), asumiendo que el usuario ya existe en users
     */
    fun createFollowerProfile(id: Int) {
        val follower = Follower(
            UserId(id),
            UserName(""),  // No se usa en la inserción de followers
            UserEmail(""), // No se usa en la inserción de followers
            UserPsswd(""), // No se usa en la inserción de followers
            UserRol("follower") // No se usa en la inserción de followers
        )
        followerRepo.create(follower)
    }

    fun edit(oldId: Int, newId: Int, name: String, email: String, passwd: String, rol: String) {
        followerRepo.getOneById(UserId(oldId)) ?: throw UserNotFoundError("follower not found")

        if (oldId != newId) {
            val conflict = followerRepo.getOneById(UserId(newId))
            if (conflict != null) throw IllegalArgumentException("follower with id $newId already exists")

            val created = Follower(
                UserId(newId),
                UserName(name),
                UserEmail(email),
                UserPsswd(passwd),
                UserRol(rol)
            )
            followerRepo.create(created)
            followerRepo.delete(UserId(oldId))
            return
        }

        val updated = Follower(
            UserId(oldId),
            UserName(name),
            UserEmail(email),
            UserPsswd(passwd),
            UserRol(rol)
        )
        followerRepo.edit(updated)
    }

    fun delete(id: Int) {
        followerRepo.getOneById(UserId(id)) ?: throw UserNotFoundError("follower not found")
        followerRepo.delete(UserId(id))
    }
}