package com.example.quizapp.data.remote

import com.example.quizapp.data.local.QuestionDao
import com.example.quizapp.data.local.QuizDao
import com.example.quizapp.data.mapper.toDomain
import com.example.quizapp.data.mapper.toEntity
import com.example.quizapp.domain.Question
import com.example.quizapp.domain.Quiz
import com.example.quizapp.domain.Result
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

/**
 * Implementação do repositório de quizzes.
 * Sincroniza dados do Firestore para o Room e sempre lê do banco local.
 *
 * Estrutura no Firestore:
 * - /quizzes/{quizId} → dados do quiz
 * - /quizzes/{quizId}/questions/{questionId} → questões do quiz
 */
class QuizRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val quizDao: QuizDao,
    private val questionDao: QuestionDao
) : QuizRepository {

    override fun getQuizzes(): Flow<List<Quiz>> {
        return quizDao.getAllQuizzes().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getQuestionsByQuiz(quizId: String): Flow<List<Question>> {
        return questionDao.getQuestionsByQuiz(quizId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun syncFromFirebase(): Result<Unit> {
        return try {
            // 1. Buscar todos os quizzes do Firestore
            val quizzesSnapshot = firestore.collection("quizzes").get().await()

            val quizzes = mutableListOf<Quiz>()
            val allQuestions = mutableListOf<Question>()

            for (doc in quizzesSnapshot.documents) {
                val id = doc.id
                val title = doc.getString("title") ?: ""
                val subtitle = doc.getString("subtitle") ?: ""
                val time = (doc.getLong("time") ?: 10L).toInt()

                // 2. Buscar questões de cada quiz (subcoleção)
                val questionsSnapshot = firestore.collection("quizzes")
                    .document(id)
                    .collection("questions")
                    .get()
                    .await()

                val questions = questionsSnapshot.documents.mapNotNull { qDoc ->
                    try {
                        @Suppress("UNCHECKED_CAST")
                        val options = qDoc.get("options") as? List<String> ?: emptyList()
                        Question(
                            id = qDoc.id,
                            quizId = id,
                            question = qDoc.getString("question") ?: "",
                            options = options,
                            correct = qDoc.getString("correct") ?: ""
                        )
                    } catch (e: Exception) {
                        null
                    }
                }

                quizzes.add(
                    Quiz(
                        id = id,
                        title = title,
                        subtitle = subtitle,
                        timeMinutes = time,
                        questionCount = questions.size
                    )
                )
                allQuestions.addAll(questions)
            }

            // 3. Limpar dados antigos e inserir novos no Room
            quizDao.deleteAll()
            questionDao.deleteAll()
            quizDao.insertAll(quizzes.map { it.toEntity() })
            questionDao.insertAll(allQuestions.map { it.toEntity() })

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun hasLocalData(): Boolean {
        return quizDao.getCount() > 0
    }
}
