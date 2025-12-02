package com.adapp.backend.SocialMedia.Domain.Repositories

import com.adapp.backend.SocialMedia.Domain.Models.SocialMedia
import com.adapp.backend.SocialMedia.Domain.Models.SocialMediaId
import com.adapp.backend.User.Domain.Models.UserId

interface SocialMediaRepository {
    fun create(SM: SocialMedia)
    fun edit(SM: SocialMedia)
    fun delete(id: SocialMediaId)
    fun getAllSM(): List<SocialMedia>
    fun getOneById(id: SocialMediaId): SocialMedia?
    fun getByArtistId(artistId: UserId): List<SocialMedia>
}