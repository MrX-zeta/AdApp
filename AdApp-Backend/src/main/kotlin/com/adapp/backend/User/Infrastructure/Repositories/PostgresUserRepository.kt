package com.adapp.backend.User.Infrastructure.Repositories

import com.adapp.backend.Shared.Infrastructure.Database.Tables.UsersTable
import com.adapp.backend.User.Domain.Models.*
import com.adapp.backend.User.Domain.Repositories.UserRepository
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class PostgresUserRepository : UserRepository {

    override fun create(user: User) {
        transaction {
            UsersTable.insert {
                it[nombre] = user.nombre.value
                it[correo] = user.correo.value
                it[contrasena] = user.contrasena.value
                it[rol] = user.rol.value
            }
        }
    }

    override fun edit(user: User) {
        transaction {
            UsersTable.update({ UsersTable.id eq user.Usuarioid.value }) {
                it[nombre] = user.nombre.value
                it[correo] = user.correo.value
                it[contrasena] = user.contrasena.value
                it[rol] = user.rol.value
            }
        }
    }

    override fun getAllUsers(): List<User> {
        return transaction {
            UsersTable.selectAll().map { rowToUser(it) }
        }
    }

    override fun getOneById(id: UserId): User? {
        return transaction {
            UsersTable.selectAll()
                .where { UsersTable.id eq id.value }
                .map { rowToUser(it) }
                .singleOrNull()
        }
    }

    override fun delete(id: UserId) {
        transaction {
            UsersTable.deleteWhere(limit = null, op = {
                UsersTable.id eq id.value
            })
        }
    }

    private fun rowToUser(row: ResultRow): User {
        return User(
            UserId(row[UsersTable.id]),
            UserName(row[UsersTable.nombre] ?: ""),
            UserEmail(row[UsersTable.correo]),
            UserPsswd(row[UsersTable.contrasena] ?: ""),
            UserRol(row[UsersTable.rol] ?: "")
        )
    }
}

