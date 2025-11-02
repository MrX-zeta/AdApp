package com.adapp.backend.Artist.Domain.Models

@JvmInline
value class ArtistSocialMedia(
    val value: String
){
    init {
        EnsureIsValid()
    }

    private fun EnsureIsValid(){
        if (this.value == "") throw IllegalArgumentException("value cannot be '' ")
    }
}

