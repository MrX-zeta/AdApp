package com.adapp.backend.User.Domain.Models

data class UserEmail(
    val value : String
){
    override fun toString(): String = value
}