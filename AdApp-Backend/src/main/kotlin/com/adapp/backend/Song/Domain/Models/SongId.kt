package com.adapp.backend.Song.Domain.Models

@JvmInline
value class SongId(
    val value: Int
) {
    private fun EnsureIsValid(){
        if(this.value <= 0){
            throw IllegalArgumentException("SongId no puede ser menor o igual a 0")
        }
    }
}