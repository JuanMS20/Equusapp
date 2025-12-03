package com.villalobos.caballoapp.data.source.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.villalobos.caballoapp.data.model.BadgeEntity
import com.villalobos.caballoapp.data.model.UserProgressEntity

@Database(
    entities = [UserProgressEntity::class, BadgeEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gamificationDao(): GamificationDao
}