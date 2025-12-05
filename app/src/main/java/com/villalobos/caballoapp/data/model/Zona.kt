package com.villalobos.caballoapp.data.model

data class Zona (
    val id: Int,
<<<<<<< Updated upstream
    val nombre: String, // Ejemplo: "Zona parieto-temporal"
    val regionId: Int,
    val musculos: List<Musculo>, // Lista musculos

    // Coordenadas
    val hotspotX: Float,
    val hotspotY: Float,

    val descripcionCorta: String = "",
    val imagenMapa: String? = null // Imagen que representa la SubZona en el mapa

=======
    val nombre: String, // El nombre de la zona, ej: "Zona parieto-temporal"
    val regionId: Int,
    val hotspotX: Float, // Coordenadas para el mapa principal
    val hotspotY: Float,
    val hotspotNumero: Int,
    val descripcionCorta: String = "",
    val imagenMapa: String? = null,
    val musculos: List<Musculo> // Lista de los músculos que contiene
>>>>>>> Stashed changes
)