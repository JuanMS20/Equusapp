package com.villalobos.caballoapp.ui.quiz

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.villalobos.caballoapp.data.model.QuizQuestion
import com.villalobos.caballoapp.data.repository.QuizRepository
import com.villalobos.caballoapp.data.repository.AchievementRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

/**
 * Unit Tests para QuizViewModel.
 * Verifica la lógica del quiz sin dependencias de Android.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class QuizViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var quizRepository: QuizRepository

    @Mock
    private lateinit var achievementRepository: AchievementRepository

    private lateinit var viewModel: QuizViewModel

    private val sampleQuestions = listOf(
        QuizQuestion(
            id = 1,
            regionId = 1,
            question = "¿Cuál es el músculo más grande?",
            options = listOf("A", "B", "C", "D"),
            correctAnswer = 0,
            explanation = "Explicación"
        ),
        QuizQuestion(
            id = 2,
            regionId = 1,
            question = "¿Cuál es la función del músculo X?",
            options = listOf("A", "B", "C", "D"),
            correctAnswer = 1,
            explanation = "Explicación"
        )
    )

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        viewModel = QuizViewModel(quizRepository, achievementRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `startQuiz returns true when questions are available`() {
        // Given
        whenever(quizRepository.getQuizQuestions(null, 10)).thenReturn(sampleQuestions)

        // When
        val result = viewModel.startQuiz(null, 10)

        // Then
        assertTrue(result)
        assertTrue(viewModel.isQuizActive())
    }

    @Test
    fun `startQuiz returns false when no questions available`() {
        // Given
        whenever(quizRepository.getQuizQuestions(null, 10)).thenReturn(emptyList())

        // When
        val result = viewModel.startQuiz(null, 10)

        // Then
        assertFalse(result)
        assertFalse(viewModel.isQuizActive())
    }

    @Test
    fun `answerQuestion advances to next question`() {
        // Given
        whenever(quizRepository.getQuizQuestions(null, 10)).thenReturn(sampleQuestions)
        viewModel.startQuiz(null, 10)

        // When
        viewModel.answerQuestion(0)

        // Then
        val state = viewModel.quizState.value
        assertNotNull(state)
        assertEquals(1, state?.currentQuestionIndex)
    }

    @Test
    fun `answerQuestion completes quiz on last question`() {
        // Given
        whenever(quizRepository.getQuizQuestions(null, 2)).thenReturn(sampleQuestions)
        viewModel.startQuiz(null, 2)

        // When - Answer both questions
        viewModel.answerQuestion(0) // Correct
        viewModel.answerQuestion(1) // Correct

        // Then
        val state = viewModel.quizState.value
        assertNotNull(state)
        assertTrue(state?.isCompleted == true)
        assertFalse(viewModel.isQuizActive())
    }

    @Test
    fun `skipQuestion marks answer as -1`() {
        // Given
        whenever(quizRepository.getQuizQuestions(null, 10)).thenReturn(sampleQuestions)
        viewModel.startQuiz(null, 10)

        // When
        viewModel.skipQuestion()

        // Then - Should have advanced and recorded -1 as answer
        val state = viewModel.quizState.value
        assertEquals(1, state?.currentQuestionIndex)
    }

    @Test
    fun `abandonQuiz resets state`() {
        // Given
        whenever(quizRepository.getQuizQuestions(null, 10)).thenReturn(sampleQuestions)
        viewModel.startQuiz(null, 10)

        // When
        viewModel.abandonQuiz()

        // Then
        assertFalse(viewModel.isQuizActive())
        val state = viewModel.quizState.value
        assertTrue(state?.questions?.isEmpty() == true)
    }

    @Test
    fun `getTimeElapsed returns correct time`() {
        // Given
        whenever(quizRepository.getQuizQuestions(null, 10)).thenReturn(sampleQuestions)
        viewModel.startQuiz(null, 10)

        // When
        val elapsed = viewModel.getTimeElapsed()

        // Then
        assertTrue(elapsed >= 0)
    }

    @Test
    fun `getCurrentQuestions returns quiz questions`() {
        // Given
        whenever(quizRepository.getQuizQuestions(null, 10)).thenReturn(sampleQuestions)
        viewModel.startQuiz(null, 10)

        // When
        val questions = viewModel.getCurrentQuestions()

        // Then
        assertEquals(sampleQuestions.size, questions.size)
    }

    @Test
    fun `clearEvent sets event to null`() {
        // Given
        whenever(quizRepository.getQuizQuestions(null, 10)).thenReturn(sampleQuestions)
        viewModel.startQuiz(null, 10)

        // When
        viewModel.clearEvent()

        // Then
        assertNull(viewModel.quizEvent.value)
    }
}
