package com.villalobos.caballoapp.data.repository

import com.villalobos.caballoapp.data.model.BadgeEntity
import com.villalobos.caballoapp.data.model.UserProgressEntity
import com.villalobos.caballoapp.data.source.local.GamificationDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GamificationRepository @Inject constructor(
    private val gamificationDao: GamificationDao
) {

    // User Progress
    val userProgress: Flow<UserProgressEntity?> = gamificationDao.getUserProgress()

    suspend fun saveUserProgress(progress: UserProgressEntity) {
        gamificationDao.insertUserProgress(progress)
    }

    suspend fun updateUserProgress(progress: UserProgressEntity) {
        gamificationDao.updateUserProgress(progress)
    }

    suspend fun addXp(xp: Long) {
        gamificationDao.addXp(xp)
    }

    // Badges
    val allBadges: Flow<List<BadgeEntity>> = gamificationDao.getAllBadges()

    suspend fun insertBadges(badges: List<BadgeEntity>) {
        gamificationDao.insertBadges(badges)
    }

    suspend fun unlockBadge(badgeId: String) {
        gamificationDao.unlockBadge(badgeId)
    }

    // Initial data
    suspend fun initializeDataIfNeeded() {
        if (gamificationDao.getUserProgressCount() == 0) {
            val initialProgress = UserProgressEntity()
            gamificationDao.insertUserProgress(initialProgress)
        }
        if (gamificationDao.getBadgesCount() == 0) {
            val initialBadges = listOf(
                BadgeEntity(
                    id = "badge_first_quiz",
                    name = "Primer Quiz",
                    description = "Completa tu primer quiz",
                    iconResId = android.R.drawable.ic_menu_agenda // Placeholder, replace with actual drawable
                ),
                BadgeEntity(
                    id = "badge_perfect_score",
                    name = "Puntuación Perfecta",
                    description = "Obtén 100% en un quiz",
                    iconResId = android.R.drawable.ic_menu_compass
                ),
                // Add more as needed
            )
            gamificationDao.insertBadges(initialBadges)
        }
    }
}