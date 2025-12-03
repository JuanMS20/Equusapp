package com.villalobos.caballoapp.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.villalobos.caballoapp.data.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val quizRepository: QuizRepository
) : ViewModel() {

    private val _stats = MutableStateFlow<StatsData?>(null)
    val stats: StateFlow<StatsData?> = _stats

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            val userStats = quizRepository.getUserStats()
            
            _stats.value = StatsData(
                totalQuizzes = userStats.totalQuizzes,
                bestScore = userStats.bestScore,
                perfectQuizzes = userStats.perfectQuizzes,
                fastestTime = userStats.fastestQuizTime,
                musclesStudied = userStats.musclesStudied,
                totalMuscles = 120, // Total aproximado de músculos en la app
                studyStreak = userStats.studyStreak,
                regionScores = userStats.regionScores.mapKeys { it.key.toString() }
            )
        }
    }

    data class StatsData(
        val totalQuizzes: Int,
        val bestScore: Int,
        val perfectQuizzes: Int,
        val fastestTime: Long,
        val musclesStudied: Int,
        val totalMuscles: Int,
        val studyStreak: Int,
        val regionScores: Map<String, Int>
    )
}
