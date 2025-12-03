package com.villalobos.caballoapp.data.source

/**
 * Clase de datos para estadísticas del usuario.
 */
data class UserStats(
    val totalQuizzes: Int = 0,
    val bestScore: Int = 0,
    val musclesStudied: Int = 0,
    val studyStreak: Int = 0,
    val perfectQuizzes: Int = 0,
    val fastestQuizTime: Long = Long.MAX_VALUE,
    val regionScores: Map<Int, Int> = emptyMap(),
    // Nuevos campos para gamificación
    val totalXp: Int = 0,
    val level: Int = 1,
    val lastQuizDate: Long = 0L // Timestamp en ms
) {
    fun getRegionScore(regionId: Int): Int {
        return regionScores[regionId] ?: 0
    }
}
