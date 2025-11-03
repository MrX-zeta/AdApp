package com.adapp.backend.Artist.Domain.Models

@JvmInline
value class ArtistContactNum(
    val value: String
){
    init {
        EnsureIsValid()
    }

    private fun EnsureIsValid(){
        if(this.value == ""){
            throw Error("UserId cannot be negative")
        }
    }
}
