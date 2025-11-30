package com.adapp.backend.Follower.Infrastructure.Repositories

import com.adapp.backend.Follower.Domain.Models.Follower
import com.adapp.backend.Follower.Domain.Repsitories.FollowerRepository
import com.adapp.backend.Shared.Infrastructure.Database.Tables.FollowersTable
import com.adapp.backend.Shared.Infrastructure.Database.Tables.UsersTable
import com.adapp.backend.User.Domain.Models.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class PostgresFollowerRepository : FollowerRepository {

    override fun create(follower: Follower) {
        transaction {
            // Solo insertar en followers, asumiendo que el usuario ya existe en users
            FollowersTable.insert {
                it[id] = follower.Usuarioid.value
            }
        }
    }

    override fun edit(follower: Follower) {
        transaction {
            // Actualizar users
            UsersTable.update({ UsersTable.id eq follower.Usuarioid.value }) {
                it[nombre] = follower.nombre.value
                it[correo] = follower.correo.value
                it[contrasena] = follower.contrasena.value
                it[rol] = follower.rol.value
            }
            // Nota: FollowersTable solo tiene ID, no hay más campos que actualizar
        }
    }

    override fun getAllFollowers(): List<Follower> {
        return transaction {
            (UsersTable innerJoin FollowersTable)
                .selectAll()
                .where { UsersTable.rol eq "follower" }
                .map { rowToFollower(it) }
        }
    }

    override fun getOneById(id: UserId): Follower? {
        return transaction {
            (UsersTable innerJoin FollowersTable)
                .selectAll()
                .where { UsersTable.id eq id.value }
                .map { rowToFollower(it) }
                .singleOrNull()
        }
    }

    override fun delete(id: UserId) {
        transaction {
            // Gracias a ON DELETE CASCADE, solo necesitamos eliminar de users
            UsersTable.deleteWhere(limit = null, op = {
                UsersTable.id eq id.value
            })
        }
    }

    private fun rowToFollower(row: ResultRow): Follower {
        return Follower(
            FollowerId = UserId(row[UsersTable.id]),
            nombre = UserName(row[UsersTable.nombre] ?: ""),
            correo = UserEmail(row[UsersTable.correo]),
            contrasena = UserPsswd(row[UsersTable.contrasena] ?: ""),
            rol = UserRol(row[UsersTable.rol] ?: "follower")
        )
    }
}

