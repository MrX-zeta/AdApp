package com.adapp.backend.SocialMedia.Application.SocialMediaGetOneById

import com.adapp.backend.SocialMedia.Domain.Exceptions.SocialMediaNotFoundError
import com.adapp.backend.SocialMedia.Domain.Models.SocialMedia
import com.adapp.backend.SocialMedia.Domain.Models.SocialMediaId
import com.adapp.backend.SocialMedia.Domain.Repositories.SocialMediaRepository

class SocialMediaGetOneById(private val SMRepo: SocialMediaRepository) {
    suspend fun getOneById(id: Int): SocialMedia?{
        val SM = SMRepo.getOneById(SocialMediaId(id))

        if(SM == null) throw SocialMediaNotFoundError("SocialMedia not found")
        return SM
    }
}