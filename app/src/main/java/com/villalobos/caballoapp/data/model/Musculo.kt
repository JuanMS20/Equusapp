package com.villalobos.caballoapp.data.model

data class Musculo(
    val id: Int,
    val nombre: String,
    val origen: String,
    val insercion: String,
    val funcion: String, // Contiene la Biomecánica
    val regionId: Int,
<<<<<<< Updated upstream
=======
    val descripcion: String = "",
    val imagen: String? = null // Imagen de detalle del músculo, no la zona

>>>>>>> Stashed changes
)