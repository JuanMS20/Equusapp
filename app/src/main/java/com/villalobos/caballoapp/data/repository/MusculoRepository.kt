package com.villalobos.caballoapp.data.repository

import com.villalobos.caballoapp.DatosMusculares
import com.villalobos.caballoapp.Musculo
import com.villalobos.caballoapp.Region
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository para manejar datos de músculos.
 * Centraliza el acceso a datos de músculos y regiones anatómicas.
 */
@Singleton
class MusculoRepository @Inject constructor() {

    // ============ Regiones ============

    /**
     * Obtiene todas las regiones disponibles.
     */
    fun getAllRegions(): List<Region> {
        return DatosMusculares.regiones
    }

    /**
     * Obtiene una región por su ID.
     */
    fun getRegionById(id: Int): Region? {
        return DatosMusculares.obtenerRegionPorId(id)
    }

    /**
     * Obtiene el nombre completo de una región.
     */
    fun getRegionName(regionId: Int): String {
        return DatosMusculares.obtenerRegionPorId(regionId)?.nombreCompleto ?: "Región desconocida"
    }

    // ============ Músculos ============

    /**
     * Obtiene todos los músculos.
     */
    fun getAllMuscles(): List<Musculo> {
        return DatosMusculares.obtenerTodosLosMusculos()
    }

    /**
     * Obtiene los músculos de una región específica.
     */
    fun getMusclesByRegion(regionId: Int): List<Musculo> {
        return DatosMusculares.obtenerMusculosPorRegion(regionId)
    }

    /**
     * Obtiene un músculo por su ID.
     */
    fun getMuscleById(id: Int): Musculo? {
        return DatosMusculares.obtenerMusculoPorId(id)
    }

    /**
     * Busca músculos por nombre.
     */
    fun searchMuscles(query: String): List<Musculo> {
        if (query.isBlank()) return emptyList()
        
        val lowerQuery = query.lowercase()
        return getAllMuscles().filter { musculo ->
            musculo.nombre.lowercase().contains(lowerQuery) ||
            musculo.descripcion.lowercase().contains(lowerQuery)
        }
    }

    /**
     * Obtiene el conteo de músculos por región.
     */
    fun getMuscleCountByRegion(regionId: Int): Int {
        return getMusclesByRegion(regionId).size
    }

    /**
     * Obtiene el conteo total de músculos.
     */
    fun getTotalMuscleCount(): Int {
        return getAllMuscles().size
    }

    // ============ Estadísticas ============

    /**
     * Obtiene estadísticas de músculos por región.
     */
    fun getMuscleStatsByRegion(): Map<Region, Int> {
        return getAllRegions().associateWith { region ->
            getMuscleCountByRegion(region.id)
        }
    }

    /**
     * Verifica si una región tiene músculos.
     */
    fun regionHasMuscles(regionId: Int): Boolean {
        return getMusclesByRegion(regionId).isNotEmpty()
    }
}
