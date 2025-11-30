package com.adapp.backend.FollowerArtist.Infraestructure.Repositories

import com.adapp.backend.FollowerArtist.Domain.Models.FollowerArtist
import com.adapp.backend.FollowerArtist.Domain.Repositories.FollowerArtistRepository
import com.adapp.backend.Shared.Infrastructure.Database.Tables.FollowerArtistTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class PostgresFollowerArtistRepository : FollowerArtistRepository {

    override fun create(followerArtist: FollowerArtist) {
        transaction {
            // Verificar si ya existe antes de insertar para evitar duplicados
            if (!exists(followerArtist.followerId, followerArtist.artistId)) {
                FollowerArtistTable.insert {
                    it[followerId] = followerArtist.followerId
                    it[artistId] = followerArtist.artistId
                }
            }
        }
    }

    override fun delete(followerId: Int, artistId: Int) {
        transaction {
            FollowerArtistTable.deleteWhere {
                (FollowerArtistTable.followerId eq followerId) and (FollowerArtistTable.artistId eq artistId)
            }
        }
    }

    override fun getByFollowerId(followerId: Int): List<FollowerArtist> {
        return transaction {
            FollowerArtistTable.selectAll()
                .where { FollowerArtistTable.followerId eq followerId }
                .map { rowToFollowerArtist(it) }
        }
    }

    override fun getByArtistId(artistId: Int): List<FollowerArtist> {
        return transaction {
            FollowerArtistTable.selectAll()
                .where { FollowerArtistTable.artistId eq artistId }
                .map { rowToFollowerArtist(it) }
        }
    }

    override fun getAll(): List<FollowerArtist> {
        return transaction {
            FollowerArtistTable.selectAll()
                .map { rowToFollowerArtist(it) }
        }
    }

    override fun exists(followerId: Int, artistId: Int): Boolean {
        return transaction {
            FollowerArtistTable.selectAll()
                .where {
                    (FollowerArtistTable.followerId eq followerId) and (FollowerArtistTable.artistId eq artistId)
                }
                .count() > 0
        }
    }

    private fun rowToFollowerArtist(row: ResultRow): FollowerArtist {
        return FollowerArtist(
            followerId = row[FollowerArtistTable.followerId],
            artistId = row[FollowerArtistTable.artistId]
        )
    }
}