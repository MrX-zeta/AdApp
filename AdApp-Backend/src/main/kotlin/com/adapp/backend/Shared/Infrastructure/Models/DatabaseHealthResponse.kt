package com.adapp.backend.Shared.Infrastructure.Models

import kotlinx.serialization.Serializable

@Serializable
data class DatabaseHealthResponse(
    val status: String,
    val database: String,
    val message: String? = null,
    val error: String? = null,
    val errorType: String? = null
)

