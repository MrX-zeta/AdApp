package com.adapp.backend.Song.Domain.Models


@JvmInline
value class SongTitle(
    val value: String
) {
    private fun EnsureIsValid(){
        if(this.value.isBlank()){
            throw IllegalArgumentException("SongTitle no puede estar vacio")
        }
    }
}