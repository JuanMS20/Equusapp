package com.villalobos.caballoapp.data.repository

import com.villalobos.caballoapp.data.model.Achievement
import com.villalobos.caballoapp.data.source.AchievementData
import com.villalobos.caballoapp.data.source.UserStats
import javax.inject.Inject

/**
 * Repository para gestionar logros.
 * Actúa como fachada para AchievementData y lógica relacionada.
 */
class AchievementRepository @Inject constructor() {

    fun getAllAchievements(): List<Achievement> {
        return AchievementData.achievements
    }

    fun getUnlockedAchievements(userStats: UserStats): List<Achievement> {
        return AchievementData.getUnlockedAchievements(userStats)
    }

    fun getLockedAchievements(userStats: UserStats): List<Achievement> {
        return AchievementData.getLockedAchievements(userStats)
    }

    /**
     * Compara estadísticas anteriores y actuales para detectar nuevos logros desbloqueados.
     * Útil para mostrar notificaciones.
     */
    fun getNewlyUnlockedAchievements(oldStats: UserStats, newStats: UserStats): List<Achievement> {
        val oldUnlocked = getUnlockedAchievements(oldStats).map { it.id }.toSet()
        val newUnlocked = getUnlockedAchievements(newStats)
        
        return newUnlocked.filter { !oldUnlocked.contains(it.id) }
    }
}
