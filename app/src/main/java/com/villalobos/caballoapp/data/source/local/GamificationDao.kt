package com.villalobos.caballoapp.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.villalobos.caballoapp.data.model.BadgeEntity
import com.villalobos.caballoapp.data.model.UserProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GamificationDao {

    // User Progress
    @Query("SELECT * FROM user_progress WHERE id = 1")
    fun getUserProgress(): Flow<UserProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProgress(progress: UserProgressEntity)

    @Update
    suspend fun updateUserProgress(progress: UserProgressEntity)

    @Query("UPDATE user_progress SET currentXp = currentXp + :xp WHERE id = 1")
    suspend fun addXp(xp: Long)

    // Badges
    @Query("SELECT * FROM badges")
    fun getAllBadges(): Flow<List<BadgeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBadges(badges: List<BadgeEntity>)

    @Query("UPDATE badges SET isUnlocked = 1, dateUnlocked = :date WHERE id = :badgeId")
    suspend fun unlockBadge(badgeId: String, date: Long = System.currentTimeMillis())

    // Initial data
    @Query("SELECT COUNT(*) FROM user_progress")
    suspend fun getUserProgressCount(): Int

    @Query("SELECT COUNT(*) FROM badges")
    suspend fun getBadgesCount(): Int
}