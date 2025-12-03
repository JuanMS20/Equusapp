package com.villalobos.caballoapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_progress")
data class UserProgressEntity(
    @PrimaryKey val id: Int = 1, // Siempre será 1 (usuario local)
    val currentXp: Long = 0,
    val currentRank: String = "Principiante",
    val studyStreak: Int = 0,
    val lastStudyDate: Long = 0
)