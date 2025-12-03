package com.villalobos.caballoapp.ui.quiz

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import com.villalobos.caballoapp.util.AccesibilityHelper
import com.villalobos.caballoapp.data.source.AchievementData
import com.villalobos.caballoapp.ui.base.BaseNavigationActivity
import com.villalobos.caballoapp.data.source.DatosMusculares
import com.villalobos.caballoapp.util.ErrorHandler
import com.villalobos.caballoapp.data.model.QuizQuestion
import com.villalobos.caballoapp.databinding.ActivityQuizBinding

/**
 * Activity del Quiz usando arquitectura MVVM.
 * Observa el ViewModel y actualiza la UI reactivamente.
 */
@dagger.hilt.android.AndroidEntryPoint
class QuizActivity : BaseNavigationActivity() {

    private lateinit var binding: ActivityQuizBinding
    private val viewModel: QuizViewModel by viewModels()
    
    private var regionId: Int? = null
    private var timeUpdateHandler: Handler? = null
    private var timeUpdateRunnable: Runnable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            binding = ActivityQuizBinding.inflate(layoutInflater)
            setContentView(binding.root)

            // Obtener parámetros
            regionId = intent.getIntExtra("REGION_ID", 0).takeIf { it != 0 }

            // Configurar UI
            setupUI()

            // Observar el ViewModel
            observeViewModel()

            // Iniciar quiz
            startQuiz()

            // Configurar el botón de inicio
            setupHomeButton(binding.btnHome)

            // Aplicar colores de accesibilidad
            applyActivityAccessibilityColors()

            // Configurar handler para botón de retroceso
            setupBackPressedHandler()

        } catch (e: Exception) {
            ErrorHandler.handleError(
                context = this,
                throwable = e,
                errorType = ErrorHandler.ErrorType.UNKNOWN_ERROR,
                userMessage = "Error al inicializar quiz",
                canRecover = true,
                recoveryAction = { finish() }
            )
        }
    }

    private fun setupUI() {
        // Configurar título del quiz
        val regionName = regionId?.let { id ->
            DatosMusculares.obtenerRegionPorId(id)?.nombreCompleto ?: "Anatomía General"
        } ?: "Anatomía General"

        binding.tvQuizTitle.text = "Quiz: $regionName"

        // Configurar botones
        binding.btnAnswer.setOnClickListener {
            submitAnswer()
        }

        binding.btnSkip.setOnClickListener {
            skipQuestion()
        }

        // Configurar accesibilidad
        AccesibilityHelper.setContentDescription(
            binding.btnAnswer,
            "Botón para enviar respuesta seleccionada",
            "Acción"
        )

        AccesibilityHelper.setContentDescription(
            binding.btnSkip,
            "Botón para saltar la pregunta actual",
            "Acción"
        )
    }

    private fun observeViewModel() {
        // Observar eventos del quiz
        viewModel.quizEvent.observe(this) { event ->
            when (event) {
                is QuizViewModel.QuizEvent.QuestionChanged -> {
                    updateQuestionUI(event.question, event.current, event.total)
                }
                is QuizViewModel.QuizEvent.QuizCompleted -> {
                    showQuizResults(event.result)
                }
                is QuizViewModel.QuizEvent.TimeUp -> {
                    Toast.makeText(this, "¡Tiempo agotado!", Toast.LENGTH_SHORT).show()
                }
                is QuizViewModel.QuizEvent.Error -> {
                    Toast.makeText(this, event.message, Toast.LENGTH_LONG).show()
                    finish()
                }
                null -> { /* Evento consumido */ }
            }
            // Limpiar evento después de procesarlo
            if (event != null) {
                viewModel.clearEvent()
            }
        }

        // Observar tiempo transcurrido
        viewModel.timeElapsed.observe(this) { time ->
            binding.tvTimeInfo.text = "Tiempo: ${formatTime(time)}"
        }

        // Observar nuevos logros desbloqueados
        viewModel.newAchievements.observe(this) { achievements ->
            if (achievements.isNotEmpty()) {
                showNewAchievementsDialog(achievements)
            }
        }
    }

    private fun startQuiz() {
        val success = viewModel.startQuiz(regionId, questionCount = 10)

        if (!success) {
            Toast.makeText(this, "No hay suficientes preguntas disponibles", Toast.LENGTH_LONG).show()
            finish()
        } else {
            // Iniciar actualización de tiempo
            startTimeUpdates()
        }
    }

    private fun updateQuestionUI(question: QuizQuestion, current: Int, total: Int) {
        // Actualizar progreso
        binding.tvProgress.text = "Pregunta $current de $total"
        binding.progressBar.progress = (current * 100) / total

        // Actualizar pregunta
        binding.tvQuestion.text = question.question

        // Limpiar selección anterior
        binding.radioGroupOptions.clearCheck()

        // Configurar opciones
        val radioButtons = listOf(
            binding.rbOption1,
            binding.rbOption2,
            binding.rbOption3,
            binding.rbOption4
        )

        question.options.forEachIndexed { index, option ->
            radioButtons[index].text = option
            radioButtons[index].isEnabled = true
        }

        // Configurar accesibilidad para opciones
        question.options.forEachIndexed { index, option ->
            AccesibilityHelper.setContentDescription(
                radioButtons[index],
                "Opción ${'A' + index}: $option",
                "Opción de respuesta"
            )
        }

        // Re-habilitar botones para la nueva pregunta
        setButtonsEnabled(true)
    }

    private fun submitAnswer() {
        val selectedId = binding.radioGroupOptions.checkedRadioButtonId

        if (selectedId == -1) {
            Toast.makeText(this, "Por favor selecciona una respuesta", Toast.LENGTH_SHORT).show()
            return
        }

        val radioButtons = listOf(
            binding.rbOption1,
            binding.rbOption2,
            binding.rbOption3,
            binding.rbOption4
        )

        val selectedIndex = radioButtons.indexOfFirst { it.id == selectedId }

        if (selectedIndex != -1) {
            setButtonsEnabled(false)
            viewModel.answerQuestion(selectedIndex)
        }
    }

    private fun skipQuestion() {
        AlertDialog.Builder(this)
            .setTitle("Saltar Pregunta")
            .setMessage("¿Estás seguro de que quieres saltar esta pregunta?")
            .setPositiveButton("Saltar") { _, _ ->
                setButtonsEnabled(false)
                viewModel.skipQuestion()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showQuizResults(result: QuizViewModel.QuizResult) {
        stopTimeUpdates()

        val timeFormatted = formatTime(result.timeSpent)

        // Marcar quiz como inactivo para evitar actualizaciones
        viewModel.abandonQuiz()

        AlertDialog.Builder(this)
            .setTitle("¡Quiz Completado!")
            .setMessage(buildString {
                append("Puntuación: ${result.score}%\n")
                append("Respuestas correctas: ${result.correctAnswers}/${result.totalQuestions}\n")
                append("Tiempo total: $timeFormatted\n")
                append("Tiempo promedio por pregunta: ${result.averageTimePerQuestion / 1000}s")
            })
            .setPositiveButton("Ver Respuestas Correctas") { dialog, _ ->
                dialog.dismiss()
                showCorrectAnswers(result.regionId)
                finish()
            }
            .setNegativeButton("Ver Logros") { dialog, _ ->
                dialog.dismiss()
                showAchievements()
            }
            .setNeutralButton("Continuar") { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .setCancelable(false)
            .setOnDismissListener {
                // Si el diálogo se cierra de cualquier forma, finalizar la activity
                if (!isFinishing) {
                    finish()
                }
            }
            .show()
    }

    private fun showNewAchievementsDialog(achievements: List<com.villalobos.caballoapp.data.model.Achievement>) {
        val achievementNames = achievements.joinToString("\n") { "🏆 ${it.title}" }

        AlertDialog.Builder(this)
            .setTitle("¡Nuevos Logros Desbloqueados!")
            .setMessage("¡Felicidades! Has desbloqueado:\n\n$achievementNames")
            .setIcon(android.R.drawable.star_big_on)
            .setPositiveButton("¡Genial!", null)
            .show()
    }

    private fun showAchievements() {
        val unlockedAchievements = viewModel.getUnlockedAchievements()

        if (unlockedAchievements.isNotEmpty()) {
            val achievementNames = unlockedAchievements.joinToString("\n") { "🏆 ${it.title}" }

            AlertDialog.Builder(this)
                .setTitle("Tus Logros")
                .setMessage("Has desbloqueado:\n\n$achievementNames")
                .setPositiveButton("Cerrar") { dialog, _ ->
                    dialog.dismiss()
                    finish()
                }
                .setCancelable(false)
                .show()
        } else {
            Toast.makeText(this, "Aún no tienes logros desbloqueados", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun showCorrectAnswers(regionId: Int?) {
        val finalRegionId = regionId ?: viewModel.getCurrentQuestions().firstOrNull()?.regionId ?: 1

        val intent = Intent(this, CorrectAnswersActivity::class.java)
        intent.putExtra("REGION_ID", finalRegionId)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    private fun setButtonsEnabled(enabled: Boolean) {
        binding.btnAnswer.isEnabled = enabled
        binding.btnSkip.isEnabled = enabled

        val radioButtons = listOf(
            binding.rbOption1,
            binding.rbOption2,
            binding.rbOption3,
            binding.rbOption4
        )

        radioButtons.forEach { it.isEnabled = enabled }
    }

    private fun startTimeUpdates() {
        stopTimeUpdates()

        timeUpdateHandler = Handler(Looper.getMainLooper())
        timeUpdateRunnable = object : Runnable {
            override fun run() {
                if (viewModel.isQuizActive()) {
                    viewModel.updateTimeElapsed()
                    binding.tvTimeInfo.text = "Tiempo: ${formatTime(viewModel.getTimeElapsed())}"
                    timeUpdateHandler?.postDelayed(this, 1000)
                }
            }
        }
        timeUpdateRunnable?.let { timeUpdateHandler?.post(it) }
    }

    private fun stopTimeUpdates() {
        timeUpdateRunnable?.let { runnable ->
            timeUpdateHandler?.removeCallbacks(runnable)
        }
        timeUpdateHandler?.removeCallbacksAndMessages(null)
        timeUpdateRunnable = null
        timeUpdateHandler = null
    }

    private fun formatTime(milliseconds: Long): String {
        val totalSeconds = milliseconds / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onPause() {
        super.onPause()
        stopTimeUpdates()
    }

    override fun onResume() {
        super.onResume()
        // Solo reiniciar actualizaciones si el quiz está activo y no hemos terminado
        if (viewModel.isQuizActive() && !isFinishing) {
            startTimeUpdates()
        }
    }

    override fun onDestroy() {
        stopTimeUpdates()
        // Solo abandonar si todavía está activo
        if (viewModel.isQuizActive()) {
            viewModel.abandonQuiz()
        }
        super.onDestroy()
    }

    private fun setupBackPressedHandler() {
        onBackPressedDispatcher.addCallback(this, object : androidx.activity.OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (viewModel.isQuizActive()) {
                    AlertDialog.Builder(this@QuizActivity)
                        .setTitle("Abandonar Quiz")
                        .setMessage("¿Estás seguro de que quieres salir? Perderás el progreso actual.")
                        .setPositiveButton("Salir") { _, _ ->
                            finish()
                        }
                        .setNegativeButton("Continuar", null)
                        .show()
                } else {
                    finish()
                }
            }
        })
    }

    override fun applyActivityAccessibilityColors() {
        ErrorHandler.safeExecute(
            context = this,
            errorType = ErrorHandler.ErrorType.UNKNOWN_ERROR,
            errorMessage = "Error al aplicar colores de accesibilidad en QuizActivity"
        ) {
            AccesibilityHelper.applyAccessibilityColorsToApp(this)
        }
    }
}
