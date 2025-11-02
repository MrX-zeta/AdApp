package com.adapp.backend.Follower.Domain.Models

import com.adapp.backend.User.Domain.Models.User
import com.adapp.backend.User.Domain.Models.UserEmail
import com.adapp.backend.User.Domain.Models.UserId
import com.adapp.backend.User.Domain.Models.UserName
import com.adapp.backend.User.Domain.Models.UserPsswd
import com.adapp.backend.User.Domain.Models.UserRol

class Follower(
    FollowerId: UserId,
    nombre: UserName,
    correo: UserEmail,
    contrasena: UserPsswd,
    rol: UserRol
): User(FollowerId, nombre, correo, contrasena, rol) {
    constructor(user: User): this(
        user.Usuarioid,
        user.nombre,
        user.correo,
        user.contrasena,
        user.rol
    )
    //specific methods here
}