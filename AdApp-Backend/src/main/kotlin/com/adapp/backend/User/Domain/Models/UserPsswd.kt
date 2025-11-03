package com.adapp.backend.User.Domain.Models

data class UserPsswd(
    val value: String
){
    override fun toString(): String = value
}
