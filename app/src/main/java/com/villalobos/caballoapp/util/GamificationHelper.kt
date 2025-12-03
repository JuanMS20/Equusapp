package com.villalobos.caballoapp.util

object GamificationHelper {

    // Sistema de 4 rangos con XP ajustado
    private val ranks = listOf(
        "Principiante" to 0,
        "Aprendiz" to 50,
        "Estudiante" to 150,
        "Conocedor" to 300
    )

    /**
     * Calcula el nivel numérico (1-4) basado en el XP total.
     */
    fun calculateLevel(totalXp: Int): Int {
        return when {
            totalXp >= 300 -> 4
            totalXp >= 150 -> 3
            totalXp >= 50 -> 2
            else -> 1
        }
    }

    /**
     * Calcula el rango (nombre) basado en el XP total.
     */
    fun calculateRank(totalXp: Int): String {
        return ranks.lastOrNull { totalXp >= it.second }?.first ?: "Principiante"
    }

    /**
     * Calcula el progreso hacia el siguiente nivel (0.0 a 1.0).
     */
    fun calculateLevelProgress(totalXp: Int): Float {
        val level = calculateLevel(totalXp)
        if (level >= 4) return 1f // Ya está en el máximo
        
        val currentLevelXp = ranks[level - 1].second
        val nextLevelXp = ranks[level].second
        val xpInCurrentLevel = totalXp - currentLevelXp
        val xpNeeded = nextLevelXp - currentLevelXp
        
        return (xpInCurrentLevel.toFloat() / xpNeeded).coerceIn(0f, 1f)
    }

    /**
     * Calcula el XP ganado en un quiz.
     * Base: 5 XP + bonus por score alto + bonus por perfección
     */
    fun calculateXpEarned(score: Int, isPerfect: Boolean): Int {
        var xp = 5 // Base por completar
        xp += (score / 20) * 3 // +3 XP por cada 20% de score (max 15)
        if (isPerfect) {
            xp += 10 // Bonus por perfección
        }
        return xp
    }
}
