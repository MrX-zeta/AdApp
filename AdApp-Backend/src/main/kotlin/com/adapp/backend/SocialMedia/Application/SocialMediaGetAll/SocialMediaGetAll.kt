package com.adapp.backend.SocialMedia.Application.SocialMediaGetAll

import com.adapp.backend.SocialMedia.Domain.Models.SocialMedia
import com.adapp.backend.SocialMedia.Domain.Repositories.SocialMediaRepository

class SocialMediaGetAll(private val SMRepo: SocialMediaRepository) {
    suspend fun invoke(): List<SocialMedia>{
        return SMRepo.getAllSM()
    }
}