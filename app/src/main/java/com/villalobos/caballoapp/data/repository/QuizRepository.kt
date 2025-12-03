package com.villalobos.caballoapp.data.repository

import com.villalobos.caballoapp.data.model.QuizQuestion
import com.villalobos.caballoapp.data.source.QuizData

import android.content.Context
import com.villalobos.caballoapp.data.source.UserStats

/**
 * Repository para manejar datos del Quiz.
 * Centraliza el acceso a datos de preguntas y estadísticas del usuario.
 */
class QuizRepository(private val context: Context) {

    companion object {
        private const val PREFS_NAME = "quiz_stats"
        private const val KEY_TOTAL_QUIZZES = "total_quizzes"
        private const val KEY_BEST_SCORE = "best_score"
        private const val KEY_PERFECT_QUIZZES = "perfect_quizzes"
        private const val KEY_FASTEST_TIME = "fastest_quiz_time"
        private const val KEY_REGION_SCORE_PREFIX = "region_score_"
        private const val KEY_TOTAL_XP = "total_xp"
        private const val KEY_LAST_QUIZ_DATE = "last_quiz_date"
        private const val KEY_STUDY_STREAK = "study_streak"
        private const val KEY_MUSCLES_STUDIED_SET = "muscles_studied_set"
    }

    private val prefs by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // ============ Preguntas ============

    /**
     * Obtiene preguntas aleatorias para un quiz.
     * @param regionId ID de la región (null para todas las regiones)
     * @param count Número de preguntas a obtener
     * @return Lista de preguntas mezcladas
     */
    fun getQuizQuestions(regionId: Int? = null, count: Int = 5): List<QuizQuestion> {
        return QuizData.getQuizQuestions(regionId, count)
    }

    /**
     * Obtiene preguntas de una región específica.
     */
    fun getQuestionsByRegion(regionId: Int): List<QuizQuestion> {
        return QuizData.getQuestionsByRegion(regionId)
    }

    /**
     * Obtiene una pregunta aleatoria.
     */
    fun getRandomQuestion(regionId: Int? = null): QuizQuestion {
        return QuizData.getRandomQuestion(regionId)
    }

    // ============ Estadísticas ============

    /**
     * Guarda el resultado de un quiz completado.
     */
    fun saveQuizResult(
        score: Int,
        regionId: Int?,
        timeSpent: Long
    ) {
        prefs.edit().apply {
            // Incrementar total de quizzes
            val totalQuizzes = prefs.getInt(KEY_TOTAL_QUIZZES, 0) + 1
            putInt(KEY_TOTAL_QUIZZES, totalQuizzes)

            // Actualizar mejor puntuación
            val bestScore = maxOf(prefs.getInt(KEY_BEST_SCORE, 0), score)
            putInt(KEY_BEST_SCORE, bestScore)

            // Actualizar puntuación por región
            regionId?.let { id ->
                val regionScore = prefs.getInt("$KEY_REGION_SCORE_PREFIX$id", 0)
                if (score > regionScore) {
                    putInt("$KEY_REGION_SCORE_PREFIX$id", score)
                }
            }

            // Actualizar quizzes perfectos
            if (score == 100) {
                val perfectCount = prefs.getInt(KEY_PERFECT_QUIZZES, 0) + 1
                putInt(KEY_PERFECT_QUIZZES, perfectCount)
            }

            // Actualizar tiempo más rápido
            val fastestTime = prefs.getLong(KEY_FASTEST_TIME, Long.MAX_VALUE)
            if (timeSpent < fastestTime) {
                putLong(KEY_FASTEST_TIME, timeSpent)
            }

            // --- GAMIFICACIÓN ---
            
            // 1. Calcular y guardar XP
            val currentXp = prefs.getInt(KEY_TOTAL_XP, 0)
            val xpEarned = com.villalobos.caballoapp.util.GamificationHelper.calculateXpEarned(score, score == 100)
            putInt(KEY_TOTAL_XP, currentXp + xpEarned)

            // 2. Calcular Racha (Streak)
            val lastDate = prefs.getLong(KEY_LAST_QUIZ_DATE, 0L)
            val today = java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.HOUR_OF_DAY, 0)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }.timeInMillis

            // Si la última vez fue ayer, incrementar racha. Si fue hoy, mantener. Si fue antes, reiniciar.
            val oneDayMs = 24 * 60 * 60 * 1000L
            val diff = today - lastDate

            if (lastDate == 0L) {
                // Primer quiz ever
                putInt(KEY_STUDY_STREAK, 1)
            } else if (diff == oneDayMs) {
                // Fue ayer, incrementar
                val currentStreak = prefs.getInt(KEY_STUDY_STREAK, 0)
                putInt(KEY_STUDY_STREAK, currentStreak + 1)
            } else if (diff > oneDayMs) {
                // Se rompió la racha
                putInt(KEY_STUDY_STREAK, 1)
            }
            // Si diff == 0 (fue hoy), no hacemos nada con la racha

            putLong(KEY_LAST_QUIZ_DATE, today)

            apply()
        }
    }

    /**
     * Obtiene las estadísticas del usuario.
     */
    fun getUserStats(): UserStats {
        val regionScores = mutableMapOf<Int, Int>()
        for (regionId in 1..7) {
            val score = prefs.getInt("$KEY_REGION_SCORE_PREFIX$regionId", 0)
            if (score > 0) {
                regionScores[regionId] = score
            }
        }

        val totalXp = prefs.getInt(KEY_TOTAL_XP, 0)
        val musclesStudiedSet = prefs.getStringSet(KEY_MUSCLES_STUDIED_SET, emptySet()) ?: emptySet()
        
        return UserStats(
            totalQuizzes = prefs.getInt(KEY_TOTAL_QUIZZES, 0),
            bestScore = prefs.getInt(KEY_BEST_SCORE, 0),
            musclesStudied = musclesStudiedSet.size,
            studyStreak = prefs.getInt(KEY_STUDY_STREAK, 0),
            perfectQuizzes = prefs.getInt(KEY_PERFECT_QUIZZES, 0),
            fastestQuizTime = prefs.getLong(KEY_FASTEST_TIME, Long.MAX_VALUE),
            regionScores = regionScores,
            totalXp = totalXp,
            level = com.villalobos.caballoapp.util.GamificationHelper.calculateLevel(totalXp),
            lastQuizDate = prefs.getLong(KEY_LAST_QUIZ_DATE, 0L)
        )
    }

    /**
     * Marca un músculo como estudiado.
     * @param muscleName Nombre del músculo estudiado
     * @return Cantidad de XP ganada (5 XP por nuevo músculo, 0 si ya fue estudiado)
     */
    fun markMuscleAsStudied(muscleName: String): Int {
        val currentSet = prefs.getStringSet(KEY_MUSCLES_STUDIED_SET, mutableSetOf())?.toMutableSet() ?: mutableSetOf()
        
        // Si ya fue estudiado, no dar XP adicional
        if (currentSet.contains(muscleName)) {
            return 0
        }
        
        // Agregar el músculo al set
        currentSet.add(muscleName)
        
        // Calcular XP ganada (5 XP por nuevo músculo estudiado)
        val xpEarned = 5
        val currentXp = prefs.getInt(KEY_TOTAL_XP, 0)
        
        prefs.edit().apply {
            putStringSet(KEY_MUSCLES_STUDIED_SET, currentSet)
            putInt(KEY_TOTAL_XP, currentXp + xpEarned)
            apply()
        }
        
        return xpEarned
    }

    /**
     * Verifica si un músculo ya fue estudiado.
     */
    fun isMuscleStudied(muscleName: String): Boolean {
        val currentSet = prefs.getStringSet(KEY_MUSCLES_STUDIED_SET, emptySet()) ?: emptySet()
        return currentSet.contains(muscleName)
    }

    /**
     * Obtiene la cantidad de músculos estudiados.
     */
    fun getMusclesStudiedCount(): Int {
        val currentSet = prefs.getStringSet(KEY_MUSCLES_STUDIED_SET, emptySet()) ?: emptySet()
        return currentSet.size
    }

    /**
     * Obtiene el mejor puntaje de una región específica.
     */
    fun getRegionBestScore(regionId: Int): Int {
        return prefs.getInt("$KEY_REGION_SCORE_PREFIX$regionId", 0)
    }

    /**
     * Reinicia todas las estadísticas del usuario.
     */
    fun resetStats() {
        prefs.edit().clear().apply()
    }
}
