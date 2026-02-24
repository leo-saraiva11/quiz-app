package com.example.quizapp.data.remote

import com.example.quizapp.data.local.QuizResultDao
import com.example.quizapp.data.mapper.toDomain
import com.example.quizapp.data.mapper.toEntity
import com.example.quizapp.domain.QuizResult
import com.example.quizapp.domain.Result
import com.example.quizapp.domain.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

/**
 * Implementação do repositório de resultados.
 * Salva resultados localmente (Room) e na nuvem (Firestore).
 * Provê ranking global e estatísticas por usuário.
 */
class ResultRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val quizResultDao: QuizResultDao
) : ResultRepository {

    private val resultsCollection = firestore.collection("results")

    override suspend fun saveResult(result: QuizResult): Result<Unit> {
        return try {
            // Salvar no Room
            quizResultDao.insert(result.toEntity())

            // Salvar no Firestore
            val resultMap = hashMapOf(
                "quizId" to result.quizId,
                "quizTitle" to result.quizTitle,
                "userId" to result.userId,
                "userName" to result.userName,
                "score" to result.score,
                "totalQuestions" to result.totalQuestions,
                "percentage" to result.percentage,
                "timeTakenSeconds" to result.timeTakenSeconds,
                "timestamp" to result.timestamp
            )
            resultsCollection.document(result.id).set(resultMap).await()

            // Atualizar estatísticas do usuário no Firestore
            updateUserStats(result.userId)

            Result.Success(Unit)
        } catch (e: Exception) {
            // Mesmo se falhar no Firebase, o resultado já foi salvo localmente
            Result.Error(e)
        }
    }

    override fun getUserResults(userId: String): Flow<List<QuizResult>> {
        return quizResultDao.getResultsByUser(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getRanking(): Flow<List<QuizResult>> {
        return callbackFlow {
            val listener = resultsCollection
                .orderBy("percentage", Query.Direction.DESCENDING)
                .limit(50)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        // Em vez de fechar o flow (crasha o app), emite lista vazia
                        trySend(emptyList())
                        return@addSnapshotListener
                    }
                    val results = snapshot?.documents?.mapNotNull { doc ->
                        try {
                            QuizResult(
                                id = doc.id,
                                quizId = doc.getString("quizId") ?: "",
                                quizTitle = doc.getString("quizTitle") ?: "",
                                userId = doc.getString("userId") ?: "",
                                userName = doc.getString("userName") ?: "",
                                score = (doc.getLong("score") ?: 0).toInt(),
                                totalQuestions = (doc.getLong("totalQuestions") ?: 0).toInt(),
                                percentage = doc.getDouble("percentage") ?: 0.0,
                                timeTakenSeconds = doc.getLong("timeTakenSeconds") ?: 0,
                                timestamp = doc.getLong("timestamp") ?: 0
                            )
                        } catch (e: Exception) {
                            null
                        }
                    } ?: emptyList()
                    // Ordenação secundária por tempo feita no cliente
                    val sorted = results.sortedWith(
                        compareByDescending<QuizResult> { it.percentage }
                            .thenBy { it.timeTakenSeconds }
                    )
                    trySend(sorted)
                }
            awaitClose { listener.remove() }
        }
    }

    override suspend fun getUserStats(userId: String): UserProfile {
        val count = quizResultDao.getCountByUser(userId)
        val average = quizResultDao.getAverageByUser(userId) ?: 0.0
        val best = quizResultDao.getBestByUser(userId) ?: 0.0
        return UserProfile(
            uid = userId,
            totalQuizzes = count,
            averageScore = average,
            bestScore = best
        )
    }

    override suspend fun syncResultsFromFirebase(userId: String): Result<Unit> {
        return try {
            val snapshot = resultsCollection
                .whereEqualTo("userId", userId)
                .get()
                .await()

            for (doc in snapshot.documents) {
                val result = QuizResult(
                    id = doc.id,
                    quizId = doc.getString("quizId") ?: "",
                    quizTitle = doc.getString("quizTitle") ?: "",
                    userId = doc.getString("userId") ?: "",
                    userName = doc.getString("userName") ?: "",
                    score = (doc.getLong("score") ?: 0).toInt(),
                    totalQuestions = (doc.getLong("totalQuestions") ?: 0).toInt(),
                    percentage = doc.getDouble("percentage") ?: 0.0,
                    timeTakenSeconds = doc.getLong("timeTakenSeconds") ?: 0,
                    timestamp = doc.getLong("timestamp") ?: 0
                )
                quizResultDao.insert(result.toEntity())
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Atualiza as estatísticas do usuário no Firestore após cada quiz.
     */
    private suspend fun updateUserStats(userId: String) {
        try {
            val stats = getUserStats(userId)
            firestore.collection("users")
                .document(userId)
                .update(
                    mapOf(
                        "totalQuizzes" to stats.totalQuizzes,
                        "averageScore" to stats.averageScore,
                        "bestScore" to stats.bestScore
                    )
                ).await()
        } catch (_: Exception) {
            // Silently fail — stats will be recalculated next time
        }
    }
}
