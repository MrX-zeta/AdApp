package com.adapp.backend.Follower.Application.FollowerCreate

import com.adapp.backend.Follower.Domain.Models.Follower
import com.adapp.backend.Follower.Domain.Repsitories.FollowerRepository
import com.adapp.backend.User.Domain.Models.UserEmail
import com.adapp.backend.User.Domain.Models.UserId
import com.adapp.backend.User.Domain.Models.UserName
import com.adapp.backend.User.Domain.Models.UserPsswd
import com.adapp.backend.User.Domain.Models.UserRol

class FollowerCreate(private val fllwrRepo: FollowerRepository) {
    suspend fun invoke(
        followerId: Int,
        nombre: String,
        correo: String,
        contrasena: String,
        rol: String
    ){
        val follower = Follower(
            UserId(followerId),
            UserName(nombre),
            UserEmail(correo),
            UserPsswd(contrasena),
            UserRol(rol)
        )
        fllwrRepo.create(follower)
    }
}