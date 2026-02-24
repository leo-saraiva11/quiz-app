package com.example.quizapp.domain

/**
 * Representa um quiz disponível no aplicativo.
 *
 * @property id Identificador único do quiz no Firebase/Room.
 * @property title Título do quiz (ex: "Kotlin & Android").
 * @property subtitle Subtítulo descritivo do quiz.
 * @property timeMinutes Tempo limite para completar o quiz, em minutos.
 * @property questionCount Número total de questões no quiz.
 */
data class Quiz(
    val id: String = "",
    val title: String = "",
    val subtitle: String = "",
    val timeMinutes: Int = 10,
    val questionCount: Int = 0
)
