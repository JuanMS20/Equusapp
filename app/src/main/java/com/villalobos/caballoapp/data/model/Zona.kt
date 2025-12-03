package com.villalobos.caballoapp.data.model

data class Zona (
    val id: Int,
    val nombre: String, // Ejemplo: "Zona parieto-temporal"
    val regionId: Int,
    val musculos: List<Musculo>, // Lista musculos

    // Coordenadas
    val hotspotX: Float,
    val hotspotY: Float,

    val descripcionCorta: String = "",
    val imagenMapa: String? = null // Imagen que representa la SubZona en el mapa

)