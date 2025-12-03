package com.villalobos.caballoapp.ui.quiz

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.villalobos.caballoapp.R
import com.villalobos.caballoapp.util.AccesibilityHelper
import com.villalobos.caballoapp.util.ErrorHandler
import com.villalobos.caballoapp.databinding.ActivityCorrectAnswersBinding
import com.villalobos.caballoapp.ui.quiz.CorrectAnswersViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity para mostrar las respuestas correctas del quiz.
 * Implementa MVVM delegando la lógica al CorrectAnswersViewModel.
 */
@AndroidEntryPoint
class CorrectAnswersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCorrectAnswersBinding
    private val viewModel: CorrectAnswersViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            binding = ActivityCorrectAnswersBinding.inflate(layoutInflater)
            setContentView(binding.root)

            // Observar el estado del ViewModel
            observeViewModel()
            
            // Configurar botón de volver
            setupBackButton()
            
            // Aplicar colores de accesibilidad sin mostrar errores
            try {
                AccesibilityHelper.applyAccessibilityColorsToApp(this)
            } catch (e: Exception) {
                // Silenciosamente ignorar errores de accesibilidad para no molestar al usuario
                android.util.Log.d("CorrectAnswersActivity", "Error al aplicar colores de accesibilidad: ${e.message}")
            }
            
        } catch (e: Exception) {
            ErrorHandler.handleError(
                context = this,
                throwable = e,
                errorType = ErrorHandler.ErrorType.UNKNOWN_ERROR,
                userMessage = "Error al cargar respuestas correctas",
                canRecover = true,
                recoveryAction = { finish() }
            )
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(this) { state ->
            // Actualizar título
            binding.tvTitle.text = "Respuestas Correctas - ${state.regionName}"
            
            // Mostrar respuestas
            showCorrectAnswers(state)
        }
    }

    private fun showCorrectAnswers(state: CorrectAnswersViewModel.CorrectAnswersState) {
        // Log para depuración
        android.util.Log.d("CorrectAnswersActivity", "Mostrando respuestas para región ID: ${state.regionId}")
        android.util.Log.d("CorrectAnswersActivity", "Número de preguntas encontradas: ${state.questions.size}")
        
        // Limpiar contenedor
        binding.answersContainer.removeAllViews()
        
        if (state.hasQuestions) {
            // Agregar cada pregunta con su respuesta correcta
            state.questions.forEachIndexed { index, question ->
                val correctAnswer = viewModel.getCorrectAnswerText(question)
                val questionCard = createQuestionCard(question.question, correctAnswer, index + 1)
                binding.answersContainer.addView(questionCard)
            }
        } else {
            // Mostrar un mensaje si no hay preguntas
            val noQuestionsText = TextView(this)
            noQuestionsText.text = "No hay preguntas disponibles para esta región"
            noQuestionsText.textSize = 16f
            noQuestionsText.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
            noQuestionsText.setPadding(16, 16, 16, 16)
            binding.answersContainer.addView(noQuestionsText)
        }
    }

    private fun createQuestionCard(questionText: String, correctAnswer: String, questionNumber: Int): CardView {
        val cardView = CardView(this)
        val layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.setMargins(16, 16, 16, 16)
        cardView.layoutParams = layoutParams
        
        // Configurar estilo del card
        cardView.cardElevation = 4f
        cardView.radius = 12f
        cardView.setContentPadding(24, 24, 24, 24)
        cardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.warm_cream))
        
        // Crear contenido del card
        val linearLayout = androidx.appcompat.widget.LinearLayoutCompat(this)
        linearLayout.orientation = androidx.appcompat.widget.LinearLayoutCompat.VERTICAL
        
        // TextView para el número de pregunta
        val questionNumberText = TextView(this)
        questionNumberText.text = "Pregunta $questionNumber"
        questionNumberText.textSize = 16f
        questionNumberText.setTextColor(ContextCompat.getColor(this, R.color.primary_brown))
        questionNumberText.setTypeface(null, android.graphics.Typeface.BOLD)
        linearLayout.addView(questionNumberText)
        
        // TextView para la pregunta
        val questionTextView = TextView(this)
        questionTextView.text = questionText
        questionTextView.textSize = 14f
        questionTextView.setTextColor(ContextCompat.getColor(this, R.color.text_primary))
        questionTextView.setPadding(0, 8, 0, 8)
        linearLayout.addView(questionTextView)
        
        // TextView para la respuesta correcta
        val correctAnswerTextView = TextView(this)
        correctAnswerTextView.text = "Respuesta correcta: $correctAnswer"
        correctAnswerTextView.textSize = 14f
        correctAnswerTextView.setTextColor(ContextCompat.getColor(this, R.color.correct_green))
        correctAnswerTextView.setTypeface(null, android.graphics.Typeface.BOLD)
        correctAnswerTextView.setPadding(0, 8, 0, 0)
        linearLayout.addView(correctAnswerTextView)
        
        cardView.addView(linearLayout)
        
        return cardView
    }

    private fun setupBackButton() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}