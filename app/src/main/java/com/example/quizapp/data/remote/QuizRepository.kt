package com.example.quizapp.data.remote

import com.example.quizapp.domain.Quiz
import com.example.quizapp.domain.Question
import com.example.quizapp.domain.Result
import kotlinx.coroutines.flow.Flow

/**
 * Interface do repositório de quizzes.
 * Gerencia a sincronização entre Firebase (remoto) e Room (local).
 */
interface QuizRepository {
    /** Retorna todos os quizzes disponíveis (lidos do banco local). */
    fun getQuizzes(): Flow<List<Quiz>>

    /** Retorna as questões de um quiz específico (do banco local). */
    fun getQuestionsByQuiz(quizId: String): Flow<List<Question>>

    /** Baixa quizzes e questões do Firebase e salva no Room. */
    suspend fun syncFromFirebase(): Result<Unit>

    /** Verifica se há dados locais (para decidir se precisa sincronizar). */
    suspend fun hasLocalData(): Boolean
}
