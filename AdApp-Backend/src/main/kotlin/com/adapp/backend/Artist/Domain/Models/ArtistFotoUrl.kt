package com.adapp.backend.Artist.Domain.Models

@JvmInline
value class ArtistFotoUrl(
    val value: String
){
    init {

    }

    private fun EnsureIsValid(){
        if (this.value == "") throw IllegalArgumentException("EnsureIsValid value is not allowed")
    }
}

