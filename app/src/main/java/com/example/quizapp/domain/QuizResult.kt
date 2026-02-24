package com.example.quizapp.domain

/**
 * Resultado de uma sessão de quiz completada por um usuário.
 *
 * @property id Identificador único do resultado.
 * @property quizId ID do quiz respondido.
 * @property quizTitle Título do quiz (para exibição no histórico).
 * @property userId ID do usuário que respondeu.
 * @property userName Nome de exibição do usuário.
 * @property score Número de respostas corretas.
 * @property totalQuestions Número total de questões no quiz.
 * @property percentage Percentual de acertos (0.0 a 100.0).
 * @property timeTakenSeconds Tempo gasto em segundos.
 * @property timestamp Momento em que o quiz foi finalizado (epoch millis).
 */
data class QuizResult(
    val id: String = "",
    val quizId: String = "",
    val quizTitle: String = "",
    val userId: String = "",
    val userName: String = "",
    val score: Int = 0,
    val totalQuestions: Int = 0,
    val percentage: Double = 0.0,
    val timeTakenSeconds: Long = 0,
    val timestamp: Long = System.currentTimeMillis()
)
