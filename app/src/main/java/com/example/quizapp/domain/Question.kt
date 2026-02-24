package com.example.quizapp.domain

/**
 * Representa uma questão individual de um quiz.
 *
 * @property id Identificador único da questão.
 * @property quizId ID do quiz ao qual esta questão pertence.
 * @property question Texto da pergunta.
 * @property options Lista de alternativas (geralmente 4).
 * @property correct Texto da alternativa correta.
 */
data class Question(
    val id: String = "",
    val quizId: String = "",
    val question: String = "",
    val options: List<String> = emptyList(),
    val correct: String = ""
)
