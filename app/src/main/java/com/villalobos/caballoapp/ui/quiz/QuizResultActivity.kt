package com.villalobos.caballoapp.ui.quiz

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import com.villalobos.caballoapp.R
import com.villalobos.caballoapp.databinding.ActivityQuizResultBinding
import com.villalobos.caballoapp.ui.base.BaseNavigationActivity
import com.villalobos.caballoapp.ui.main.MainActivity
import com.villalobos.caballoapp.util.AccesibilityHelper
import com.villalobos.caballoapp.util.GamificationHelper
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity para mostrar los resultados del quiz con animaciones y XP.
 */
@AndroidEntryPoint
class QuizResultActivity : BaseNavigationActivity() {

    private lateinit var binding: ActivityQuizResultBinding

    companion object {
        const val EXTRA_SCORE = "extra_score"
        const val EXTRA_CORRECT_ANSWERS = "extra_correct_answers"
        const val EXTRA_TOTAL_QUESTIONS = "extra_total_questions"
        const val EXTRA_TIME_SPENT = "extra_time_spent"
        const val EXTRA_XP_EARNED = "extra_xp_earned"
        const val EXTRA_NEW_RANK = "extra_new_rank"
        const val EXTRA_OLD_RANK = "extra_old_rank"
        const val EXTRA_REGION_ID = "extra_region_id"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        extractDataAndDisplay()
        setupButtons()
        animateEntrance()
    }

    private fun extractDataAndDisplay() {
        val score = intent.getIntExtra(EXTRA_SCORE, 0)
        val correctAnswers = intent.getIntExtra(EXTRA_CORRECT_ANSWERS, 0)
        val totalQuestions = intent.getIntExtra(EXTRA_TOTAL_QUESTIONS, 10)
        val timeSpent = intent.getLongExtra(EXTRA_TIME_SPENT, 0L)
        val xpEarned = intent.getIntExtra(EXTRA_XP_EARNED, 0)
        val newRank = intent.getStringExtra(EXTRA_NEW_RANK)
        val oldRank = intent.getStringExtra(EXTRA_OLD_RANK)

        // Set score and title based on performance
        binding.tvScore.text = "$score%"
        binding.tvCorrectAnswers.text = "$correctAnswers de $totalQuestions respuestas correctas"
        binding.tvXpEarned.text = "+$xpEarned XP"
        binding.tvTimeSpent.text = formatTime(timeSpent)

        // Set title and icon based on score
        when {
            score == 100 -> {
                binding.tvResultTitle.text = "¡Perfecto!"
                binding.ivResultIcon.setImageResource(R.drawable.ic_trophy)
            }
            score >= 80 -> {
                binding.tvResultTitle.text = "¡Excelente!"
                binding.ivResultIcon.setImageResource(R.drawable.ic_trophy)
            }
            score >= 60 -> {
                binding.tvResultTitle.text = "¡Bien hecho!"
                binding.ivResultIcon.setImageResource(R.drawable.ic_trophy)
            }
            score >= 40 -> {
                binding.tvResultTitle.text = "Sigue practicando"
                binding.ivResultIcon.setImageResource(R.drawable.ic_trophy)
            }
            else -> {
                binding.tvResultTitle.text = "¡No te rindas!"
                binding.ivResultIcon.setImageResource(R.drawable.ic_trophy)
            }
        }

        // Show level up if rank changed
        if (newRank != null && oldRank != null && newRank != oldRank) {
            binding.levelUpContainer.visibility = View.VISIBLE
            binding.tvLevelUp.text = "¡Subiste a $newRank!"
        }
    }

    private fun setupButtons() {
        binding.btnViewAnswers.setOnClickListener {
            val regionId = intent.getIntExtra(EXTRA_REGION_ID, -1)
            val intent = Intent(this, CorrectAnswersActivity::class.java)
            intent.putExtra("REGION_ID", regionId)
            startActivity(intent)
        }

        binding.btnBackToMenu.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun animateEntrance() {
        val slideUp = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)
        binding.resultCard.startAnimation(slideUp)
    }

    private fun formatTime(timeMs: Long): String {
        val seconds = (timeMs / 1000) % 60
        val minutes = (timeMs / 1000) / 60
        return String.format("%d:%02d", minutes, seconds)
    }

    override fun applyActivityAccessibilityColors() {
        try {
            val config = AccesibilityHelper.getAccessibilityConfig(this)
            AccesibilityHelper.applyBackgroundGradient(this, window.decorView, config.colorblindType)
        } catch (_: Exception) { }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}
