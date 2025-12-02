package com.adapp.backend.SocialMedia.Application.SocialMediaCreate

import com.adapp.backend.SocialMedia.Domain.Models.SocialMedia
import com.adapp.backend.SocialMedia.Domain.Models.SocialMediaId
import com.adapp.backend.SocialMedia.Domain.Models.SocialMediaUrl
import com.adapp.backend.SocialMedia.Domain.Repositories.SocialMediaRepository
import com.adapp.backend.User.Domain.Models.UserId

class SocialMediaCreate(private val SMRepo: SocialMediaRepository) {
    suspend fun invoke(
        id: Int,
        artistId: Int,
        url: String
    ){
        val SocialM = SocialMedia(
            SocialMediaId(id),
            UserId(artistId),
            SocialMediaUrl(url)
        )
        SMRepo.create(SocialM)
    }
}