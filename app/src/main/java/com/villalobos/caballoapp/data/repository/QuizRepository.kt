package com.villalobos.caballoapp.data.repository

import android.content.Context
import com.villalobos.caballoapp.data.QuizData
import com.villalobos.caballoapp.data.model.QuizQuestion
import com.villalobos.caballoapp.UserStats

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

        return UserStats(
            totalQuizzes = prefs.getInt(KEY_TOTAL_QUIZZES, 0),
            bestScore = prefs.getInt(KEY_BEST_SCORE, 0),
            musclesStudied = 0,
            studyStreak = 0,
            perfectQuizzes = prefs.getInt(KEY_PERFECT_QUIZZES, 0),
            fastestQuizTime = prefs.getLong(KEY_FASTEST_TIME, Long.MAX_VALUE),
            regionScores = regionScores
        )
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
