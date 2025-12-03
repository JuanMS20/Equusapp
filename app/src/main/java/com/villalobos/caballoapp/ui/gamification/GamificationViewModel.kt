package com.villalobos.caballoapp.ui.gamification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.villalobos.caballoapp.data.model.BadgeEntity
import com.villalobos.caballoapp.data.model.UserProgressEntity
import com.villalobos.caballoapp.data.repository.GamificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para la pantalla de Gamificación.
 * Expone el progreso del usuario y las medallas usando StateFlow.
 */
@HiltViewModel
class GamificationViewModel @Inject constructor(
    private val repository: GamificationRepository
) : ViewModel() {

    val userProgress: StateFlow<UserProgressEntity?> = repository.userProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val badges: StateFlow<List<BadgeEntity>> = repository.allBadges
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Rangos y XP requerido
    private val ranks = listOf(
        "Principiante" to 0L,
        "Aprendiz" to 50L,
        "Estudiante" to 150L,
        "Conocedor" to 300L
    )

    fun getXpRangeForRank(currentRank: String): Pair<Long, Long> {
        val currentIndex = ranks.indexOfFirst { it.first == currentRank }.coerceAtLeast(0)
        val currentXp = ranks[currentIndex].second
        val nextXp = if (currentIndex < ranks.lastIndex) {
            ranks[currentIndex + 1].second
        } else {
            ranks[currentIndex].second
        }
        return Pair(currentXp, nextXp)
    }

    fun addXp(xp: Long) {
        viewModelScope.launch {
            repository.addXp(xp)
            // Actualizar rango si es necesario
            userProgress.value?.let { progress ->
                val newXp = progress.currentXp + xp
                val newRank = calculateRank(newXp)
                if (newRank != progress.currentRank) {
                    repository.updateUserProgress(progress.copy(currentRank = newRank))
                }
            }
        }
    }

    fun unlockBadge(badgeId: String) {
        viewModelScope.launch {
            repository.unlockBadge(badgeId)
        }
    }

    private fun calculateRank(xp: Long): String {
        return ranks.lastOrNull { xp >= it.second }?.first ?: "Principiante"
    }
}
