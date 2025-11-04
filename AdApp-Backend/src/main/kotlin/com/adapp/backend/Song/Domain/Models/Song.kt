package com.adapp.backend.Song.Domain.Models

import com.adapp.backend.User.Domain.Models.UserId

class Song(
    val SongId: SongId,
    val artistId: UserId,
    val title: SongTitle,
    val url: SongUrl
) {
    fun mapToPrimitives(): Map<String, Any> {
        return mapOf(
            "id" to SongId.value,
            "artistId" to artistId.value,
            "title" to title.value,
            "url" to url.value
        )
    }
}