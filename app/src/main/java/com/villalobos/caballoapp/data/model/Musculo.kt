package com.villalobos.caballoapp.data.model

data class Musculo(
    val id: Int,
    val nombre: String,
    val origen: String,
    val insercion: String,
    val funcion: String, // Contiene la Biomecánica
    val regionId: Int,
)