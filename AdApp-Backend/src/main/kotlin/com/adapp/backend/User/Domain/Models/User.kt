package com.adapp.backend.User.Domain.Models

import com.adapp.examples.UserService.Users.id

open class User(
    val Usuarioid: UserId,
    val nombre: UserName,
    val correo: UserEmail,
    val contrasena: UserPsswd,
    val rol: UserRol
){
    fun mapToPrimitives(): Map<String, Any>{
        return mapOf(
            "id" to Usuarioid.value,
            "usuarioid" to Usuarioid.value,
            "nombre" to nombre.value,
            "correo" to correo.value,
            "contrasena" to contrasena.value,
            "rol" to rol.value
        )
    }
}
