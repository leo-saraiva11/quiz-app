package com.example.quizapp.data.remote

import android.util.Log
import com.example.quizapp.data.local.QuestionDao
import com.example.quizapp.data.local.QuizDao
import com.example.quizapp.data.toDomain
import com.example.quizapp.data.toEntity
import com.example.quizapp.domain.Question
import com.example.quizapp.domain.Quiz
import com.example.quizapp.domain.Result
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

/** Gemini - início
* Prompt: Crie uma implementação de repositório de quizzes que sincroniza dados do Firestore para o banco de dados local Room.
*
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
            val quizzesSnapshot = firestore.collection("quizzes").get().await()

            val quizzes = mutableListOf<Quiz>()
            val allQuestions = mutableListOf<Question>()

            for (doc in quizzesSnapshot.documents) {
                val id = doc.id
                var title = doc.getString("title") ?: ""
                val subtitle = doc.getString("subtitle") ?: ""
                val time = (doc.getLong("time") ?: 10L).toInt()

                // Se o título estiver vindo como o subtítulo técnico, corrigimos aqui
                if (title == "SQL, NoSQL e Modelagem de Dados" || subtitle == "SQL, NoSQL e Modelagem de Dados") {
                    title = "Banco de Dados"
                } else if (title == "Conceitos de IA e Machine Learning" || subtitle == "Conceitos de IA e Machine Learning") {
                    title = "Inteligência Artificial"
                } else if (title == "Protocolos, Internet e Comunicação" || subtitle == "Protocolos, Internet e Comunicação") {
                    title = "Redes de Computadores"
                }

                Log.d("QuizSync", "Buscando perguntas para o quiz: $id ($title)")

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
                            // Usamos id do quiz + id da questão para evitar colisões no banco local
                            id = "${id}_${qDoc.id}",
                            quizId = id,
                            question = qDoc.getString("question") ?: "",
                            options = options,
                            correct = qDoc.getString("correct") ?: ""
                        )
                    } catch (e: Exception) {
                        Log.e("QuizSync", "Erro ao processar questão no quiz $id: ${e.message}")
                        null
                    }
                }

                Log.d("QuizSync", "Encontradas ${questions.size} perguntas para o quiz $id")

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
/** Gemini - final */
