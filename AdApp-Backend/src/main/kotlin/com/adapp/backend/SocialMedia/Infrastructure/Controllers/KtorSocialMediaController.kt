package com.adapp.backend.SocialMedia.Infrastructure.Controllers

import com.adapp.backend.SocialMedia.Domain.Exceptions.SocialMediaNotFoundError
import com.adapp.backend.SocialMedia.Domain.Models.SocialMedia
import com.adapp.backend.SocialMedia.Domain.Models.SocialMediaDTO
import com.adapp.backend.SocialMedia.Domain.Models.SocialMediaId
import com.adapp.backend.SocialMedia.Domain.Models.SocialMediaUrl
import com.adapp.backend.SocialMedia.Domain.Repositories.SocialMediaRepository
import com.adapp.backend.User.Domain.Models.UserId

class KtorSocialMediaController(private val SMRepo: SocialMediaRepository) {

    fun getAll(): List<SocialMediaDTO>{
        return SMRepo.getAllSM().map{SM ->
            SocialMediaDTO(
                id = SM.SocialMediaId.value,
                artistId = SM.artistId.value,
                url = SM.url.value
            )
        }
    }

    fun getOneById(id: Int): SocialMediaDTO {
        val SM = SMRepo.getOneById(SocialMediaId(id))?: throw SocialMediaNotFoundError("Social media not found")
        return SocialMediaDTO(
            id = SM.SocialMediaId.value,
            artistId = SM.artistId.value,
            url = SM.url.value
        )
    }

    fun create(id: Int, artistId: Int, url: String){
        val SM = SocialMedia(
            SocialMediaId(id),
            UserId(artistId),
            SocialMediaUrl(url)
        )
        SMRepo.create(SM)
    }

    fun edit(oldId: Int, newId: Int, artistId: Int, url: String){
        SMRepo.getOneById(SocialMediaId(oldId))?: throw SocialMediaNotFoundError("Social media not found")
        if(oldId != newId){
            val conflict = SMRepo.getOneById(SocialMediaId(newId))
            if(conflict != null) throw IllegalArgumentException("Social media with id $newId already exists")
            val created = SocialMedia(
                SocialMediaId(newId),
                UserId(artistId),
                SocialMediaUrl(url)
            )
            SMRepo.create(created)
            SMRepo.delete(SocialMediaId(oldId))
            return
        }

        val updated = SocialMedia(
            SocialMediaId(oldId),
            UserId(artistId),
            SocialMediaUrl(url)
        )
        SMRepo.edit(updated)
    }

    fun delete(id: Int){
        SMRepo.getOneById(SocialMediaId(id)) ?: throw SocialMediaNotFoundError("Social media not found")
        SMRepo.delete(SocialMediaId(id))
    }

}