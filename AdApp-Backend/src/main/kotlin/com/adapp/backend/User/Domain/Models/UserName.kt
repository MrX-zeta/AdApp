package com.adapp.backend.User.Domain.Models

data class UserName(
    val value: String
){
    override fun toString(): String = value
}
