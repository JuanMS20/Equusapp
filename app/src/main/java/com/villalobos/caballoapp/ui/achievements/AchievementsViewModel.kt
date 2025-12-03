package com.villalobos.caballoapp.ui.achievements

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.villalobos.caballoapp.data.model.Achievement
import com.villalobos.caballoapp.data.repository.AchievementRepository
import com.villalobos.caballoapp.data.repository.QuizRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AchievementsViewModel @Inject constructor(
    private val achievementRepository: AchievementRepository,
    private val quizRepository: QuizRepository
) : ViewModel() {

    private val _achievements = MutableLiveData<List<AchievementItem>>()
    val achievements: LiveData<List<AchievementItem>> = _achievements

    init {
        loadAchievements()
    }

    fun loadAchievements() {
        viewModelScope.launch {
            val userStats = quizRepository.getUserStats()
            val allAchievements = achievementRepository.getAllAchievements()
            val unlockedAchievements = achievementRepository.getUnlockedAchievements(userStats).map { it.id }.toSet()

            val items = allAchievements.map { achievement ->
                AchievementItem(
                    achievement = achievement,
                    isUnlocked = unlockedAchievements.contains(achievement.id)
                )
            }
            _achievements.value = items
        }
    }

    data class AchievementItem(
        val achievement: Achievement,
        val isUnlocked: Boolean
    )
}
