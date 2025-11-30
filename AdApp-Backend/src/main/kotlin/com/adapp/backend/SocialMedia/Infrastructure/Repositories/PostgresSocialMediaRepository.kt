package com.adapp.backend.SocialMedia.Infrastructure.Repositories

import com.adapp.backend.Shared.Infrastructure.Database.Tables.SocialMediaTable
import com.adapp.backend.SocialMedia.Domain.Models.SocialMedia
import com.adapp.backend.SocialMedia.Domain.Models.SocialMediaId
import com.adapp.backend.SocialMedia.Domain.Models.SocialMediaUrl
import com.adapp.backend.SocialMedia.Domain.Repositories.SocialMediaRepository
import com.adapp.backend.User.Domain.Models.UserId
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class PostgresSocialMediaRepository : SocialMediaRepository {

    override fun create(SM: SocialMedia) {
        transaction {
            SocialMediaTable.insert {
                it[artistId] = SM.artistId.value
                it[url] = SM.url.value
            }
        }
    }

    override fun edit(SM: SocialMedia) {
        transaction {
            SocialMediaTable.update({ SocialMediaTable.id eq SM.SocialMediaId.value }) {
                it[artistId] = SM.artistId.value
                it[url] = SM.url.value
            }
        }
    }

    override fun delete(id: SocialMediaId) {
        transaction {
            SocialMediaTable.deleteWhere(limit = null, op = {
                SocialMediaTable.id eq id.value
            })
        }
    }

    override fun getAllSM(): List<SocialMedia> {
        return transaction {
            SocialMediaTable.selectAll().map { rowToSocialMedia(it) }
        }
    }

    override fun getOneById(id: SocialMediaId): SocialMedia? {
        return transaction {
            SocialMediaTable.selectAll()
                .where { SocialMediaTable.id eq id.value }
                .map { rowToSocialMedia(it) }
                .singleOrNull()
        }
    }

    override fun getByArtistId(artistId: UserId): List<SocialMedia> {
        return transaction {
            SocialMediaTable.selectAll()
                .where { SocialMediaTable.artistId eq artistId.value }
                .map { rowToSocialMedia(it) }
        }
    }

    private fun rowToSocialMedia(row: ResultRow): SocialMedia {
        return SocialMedia(
            SocialMediaId = SocialMediaId(row[SocialMediaTable.id]),
            artistId = UserId(row[SocialMediaTable.artistId]),
            url = SocialMediaUrl(row[SocialMediaTable.url] ?: "")
        )
    }
}

