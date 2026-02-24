package com.example.quizapp.domain

/**
 * Perfil do usuário com estatísticas acumuladas.
 *
 * @property uid Identificador único do Firebase Auth.
 * @property email Email do usuário.
 * @property displayName Nome de exibição do usuário.
 * @property totalQuizzes Quantidade total de quizzes completados.
 * @property averageScore Média de acertos em percentual.
 * @property bestScore Melhor pontuação em percentual.
 */
data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val totalQuizzes: Int = 0,
    val averageScore: Double = 0.0,
    val bestScore: Double = 0.0
)
