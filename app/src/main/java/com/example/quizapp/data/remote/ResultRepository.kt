package com.example.quizapp.data.remote

import com.example.quizapp.domain.QuizResult
import com.example.quizapp.domain.Result
import com.example.quizapp.domain.UserProfile
import kotlinx.coroutines.flow.Flow

/**
 * Interface do repositório de resultados.
 * Salva resultados local e remotamente, e provê ranking e estatísticas.
 */
interface ResultRepository {
    /** Salva resultado localmente (Room) e na nuvem (Firestore). */
    suspend fun saveResult(result: QuizResult): Result<Unit>

    /** Retorna o histórico de resultados do usuário (do banco local). */
    fun getUserResults(userId: String): Flow<List<QuizResult>>

    /** Retorna o ranking global (do Firestore, com fallback local). */
    fun getRanking(): Flow<List<QuizResult>>

    /** Calcula estatísticas do usuário a partir dos dados locais. */
    suspend fun getUserStats(userId: String): UserProfile

    /** Sincroniza resultados do Firestore para o banco local. */
    suspend fun syncResultsFromFirebase(userId: String): Result<Unit>
}
