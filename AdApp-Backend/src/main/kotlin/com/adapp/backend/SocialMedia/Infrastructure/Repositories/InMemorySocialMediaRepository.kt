package com.adapp.backend.SocialMedia.Infrastructure.Repositories

import com.adapp.backend.SocialMedia.Domain.Models.SocialMedia
import com.adapp.backend.SocialMedia.Domain.Models.SocialMediaId
import com.adapp.backend.SocialMedia.Domain.Repositories.SocialMediaRepository
import com.adapp.backend.User.Domain.Models.UserId

class InMemorySocialMediaRepository : SocialMediaRepository {
    private val SocialMedias = mutableListOf<SocialMedia>()

    override fun create(SM: SocialMedia) {
        SocialMedias.add(SM)
    }

    override fun edit(SM: SocialMedia) {
        val index = SocialMedias.indexOfFirst { it.SocialMediaId.value == SM.SocialMediaId.value }
        if(index >= 0) SocialMedias[index] = SM
    }

    override fun getAllSM(): List<SocialMedia> = SocialMedias.toList()

    override fun getOneById(id: SocialMediaId): SocialMedia? = SocialMedias.find { it.SocialMediaId.value == id.value }

    override fun getByArtistId(artistId: UserId): List<SocialMedia> = SocialMedias.filter { it.artistId.value == artistId.value }

    override fun delete(id: SocialMediaId) {
        SocialMedias.removeAll { it.SocialMediaId.value == id.value }
    }
}