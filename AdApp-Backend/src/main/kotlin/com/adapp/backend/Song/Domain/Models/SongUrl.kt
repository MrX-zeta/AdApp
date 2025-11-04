package com.adapp.backend.Song.Domain.Models


@JvmInline
value class SongUrl(
    val value: String
) {
    private fun EnsureIsValid(){
        if(this.value.isBlank()){
            throw IllegalArgumentException("SongUrl no puede estar vacio")
        }
    }
}