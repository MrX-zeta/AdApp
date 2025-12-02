package com.adapp.backend.Artist.Domain.Models

@JvmInline
value class ArtistContactNum(
    val value: String
){
    init {
        EnsureIsValid()
    }

    private fun EnsureIsValid(){
        // Permitir valor vacío (opcional). Si se proporciona, validar formato sencillo.
        if (this.value.isEmpty()) return

        // Validación mínima: sólo dígitos y signos +, longitud razonable
        val normalized = this.value.trim()
        val regex = Regex("^\\+?[0-9]{6,15}$")
        if (!regex.matches(normalized)) {
            throw IllegalArgumentException("Invalid contact number")
        }
    }
}
