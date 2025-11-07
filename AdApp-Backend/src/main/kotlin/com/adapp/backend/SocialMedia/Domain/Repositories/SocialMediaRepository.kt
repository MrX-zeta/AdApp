package com.adapp.backend.SocialMedia.Domain.Repositories

import com.adapp.backend.SocialMedia.Domain.Models.SocialMedia
import com.adapp.backend.SocialMedia.Domain.Models.SocialMediaId

interface SocialMediaRepository {
    fun create(SM: SocialMedia)
    fun edit(SM: SocialMedia)
    fun delete(id: SocialMediaId)
    fun getAllSM(): List<SocialMedia>
    fun getOneById(id: SocialMediaId): SocialMedia?
}