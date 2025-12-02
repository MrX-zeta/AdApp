package com.adapp.backend.User.Domain.Models


@JvmInline
value class UserId(
    val value: Int
) {
    init {
        EnsureIsValid()
    }

    private fun EnsureIsValid(){
        if(this.value < 0){
            throw Error("UserId cannot be negative")
        }
    }
}