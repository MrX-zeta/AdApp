package com.adapp.backend.SocialMedia.Application.SocialMediaDelete

import com.adapp.backend.SocialMedia.Domain.Models.SocialMediaId
import com.adapp.backend.SocialMedia.Domain.Repositories.SocialMediaRepository
import com.adapp.backend.Song.Domain.Exceptions.SongNotFoundError

class SocialMediaDelete(private val SMRepo: SocialMediaRepository) {
    suspend fun invoke(id: Int){
        val SocialMId = SocialMediaId(id)
        val SocialMediaExist = SMRepo.delete(SocialMId)

        if(SocialMediaExist == null) throw SongNotFoundError("Song Not Found")
    }
}