package com.adapp.backend.SocialMedia.Domain.Models

import com.adapp.backend.User.Domain.Models.UserId

class SocialMedia(
    val SocialMediaId: SocialMediaId,
    val artistId: UserId,
    val url: SocialMediaUrl
) {
    fun mapToPrimitives(): Map<String, Any> {
        return mapOf(
            "id" to SocialMediaId.value,
            "artistId" to artistId.value,
            "url" to url.value
        )
    }
}