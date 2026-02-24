package com.example.quizapp.ui.feature.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quizapp.data.remote.AuthRepository
import com.example.quizapp.data.remote.QuizRepository
import com.example.quizapp.data.remote.ResultRepository
import com.example.quizapp.domain.Question
import com.example.quizapp.domain.QuizResult
import com.example.quizapp.domain.Result
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID

/**
 * Estado da tela de quiz.
 */
data class QuizUiState(
    val quizTitle: String = "",
    val questions: List<Question> = emptyList(),
    val currentIndex: Int = 0,
    val selectedOption: String? = null,
    val isAnswered: Boolean = false,
    val score: Int = 0,
    val isFinished: Boolean = false,
    val totalTimeSeconds: Int = 0,
    val elapsedSeconds: Int = 0,
    val resultId: String? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

/**
 * ViewModel para execução do quiz com timer e controle de estado.
 */
class QuizViewModel(
    private val quizId: String,
    private val quizRepository: QuizRepository,
    private val resultRepository: ResultRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState = _uiState.asStateFlow()

    private var timerJob: Job? = null

    init {
        loadQuiz()
    }

    private fun loadQuiz() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val quizzes = quizRepository.getQuizzes().first()
            val quiz = quizzes.find { it.id == quizId }
            if (quiz == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Quiz não encontrado. Tente sincronizar novamente."
                )
                return@launch
            }
            val questions = quizRepository.getQuestionsByQuiz(quizId).first()
            if (questions.isEmpty()) {
                _uiState.value = _uiState.value.copy(
                    quizTitle = quiz.title,
                    isLoading = false,
                    error = "Nenhuma questão encontrada para este quiz. Tente sincronizar novamente."
                )
                return@launch
            }
            _uiState.value = _uiState.value.copy(
                quizTitle = quiz.title,
                questions = questions.shuffled(),
                totalTimeSeconds = quiz.timeMinutes * 60,
                isLoading = false
            )
            startTimer()
        }
    }

    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                val state = _uiState.value
                if (state.isFinished) break
                val newElapsed = state.elapsedSeconds + 1
                if (newElapsed >= state.totalTimeSeconds) {
                    // Tempo esgotado
                    finishQuiz()
                    break
                }
                _uiState.value = state.copy(elapsedSeconds = newElapsed)
            }
        }
    }

    fun selectOption(option: String) {
        val state = _uiState.value
        if (state.isAnswered || state.isFinished) return

        val currentQuestion = state.questions[state.currentIndex]
        val isCorrect = option == currentQuestion.correct
        val newScore = if (isCorrect) state.score + 1 else state.score

        _uiState.value = state.copy(
            selectedOption = option,
            isAnswered = true,
            score = newScore
        )
    }

    fun nextQuestion() {
        val state = _uiState.value
        if (!state.isAnswered) return

        val nextIndex = state.currentIndex + 1
        if (nextIndex >= state.questions.size) {
            finishQuiz()
        } else {
            _uiState.value = state.copy(
                currentIndex = nextIndex,
                selectedOption = null,
                isAnswered = false
            )
        }
    }

    private fun finishQuiz() {
        timerJob?.cancel()
        val state = _uiState.value
        val total = state.questions.size
        val percentage = if (total > 0) (state.score.toDouble() / total) * 100.0 else 0.0
        val resultId = UUID.randomUUID().toString()

        _uiState.value = state.copy(
            isFinished = true,
            resultId = resultId
        )

        // Salvar resultado
        viewModelScope.launch {
            val result = QuizResult(
                id = resultId,
                quizId = quizId,
                quizTitle = state.quizTitle,
                userId = authRepository.getUserId() ?: "",
                userName = authRepository.getUserDisplayName() ?: "Anônimo",
                score = state.score,
                totalQuestions = total,
                percentage = percentage,
                timeTakenSeconds = state.elapsedSeconds.toLong(),
                timestamp = System.currentTimeMillis()
            )
            resultRepository.saveResult(result)
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}
