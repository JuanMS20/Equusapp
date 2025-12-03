package com.villalobos.caballoapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "badges")
data class BadgeEntity(
    @PrimaryKey val id: String, // ej: "badge_perfect_score"
    val name: String,
    val description: String,
    val isUnlocked: Boolean = false,
    val dateUnlocked: Long? = null,
    val iconResId: Int // Guardaremos el ID del recurso drawable
)