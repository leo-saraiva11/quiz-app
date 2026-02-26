package com.example.quizapp.data

import android.content.Context
import com.example.quizapp.data.local.AppDatabase
import com.example.quizapp.data.local.entity.QuestionEntity
import com.example.quizapp.data.local.entity.QuizEntity
import com.example.quizapp.data.local.entity.QuizResultEntity
import com.example.quizapp.data.local.entity.UserProfileEntity
import com.example.quizapp.data.remote.AuthRepository
import com.example.quizapp.data.remote.AuthRepositoryImpl
import com.example.quizapp.data.remote.QuizRepository
import com.example.quizapp.data.remote.QuizRepositoryImpl
import com.example.quizapp.data.remote.ResultRepository
import com.example.quizapp.data.remote.ResultRepositoryImpl
import com.example.quizapp.domain.Question
import com.example.quizapp.domain.Quiz
import com.example.quizapp.domain.QuizResult
import com.example.quizapp.domain.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Container de injeção manual de dependências.
 * Inicializado no [QuizApplication] e provê acesso aos repositórios.
 */
object AppContainer {
    private lateinit var database: AppDatabase

    fun init(context: Context) {
        database = AppDatabase.getDatabase(context)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(
            FirebaseAuth.getInstance(),
            FirebaseFirestore.getInstance(),
            database.userProfileDao()
        )
    }

    val quizRepository: QuizRepository by lazy {
        QuizRepositoryImpl(
            FirebaseFirestore.getInstance(),
            database.quizDao(),
            database.questionDao()
        )
    }

    val resultRepository: ResultRepository by lazy {
        ResultRepositoryImpl(
            FirebaseFirestore.getInstance(),
            database.quizResultDao()
        )
    }
}

/**
 * Funções de mapeamento entre entidades Room e modelos de domínio.
 * O separador "|||" é utilizado para serializar listas de opções como String.
 */
private const val OPTIONS_SEPARATOR = "|||"

// Quiz
fun QuizEntity.toDomain() = Quiz(
    id = id,
    title = title,
    subtitle = subtitle,
    timeMinutes = timeMinutes,
    questionCount = questionCount
)

fun Quiz.toEntity() = QuizEntity(
    id = id,
    title = title,
    subtitle = subtitle,
    timeMinutes = timeMinutes,
    questionCount = questionCount
)

// Question
fun QuestionEntity.toDomain() = Question(
    id = id,
    quizId = quizId,
    question = question,
    options = options.split(OPTIONS_SEPARATOR),
    correct = correct
)

fun Question.toEntity() = QuestionEntity(
    id = id,
    quizId = quizId,
    question = question,
    options = options.joinToString(OPTIONS_SEPARATOR),
    correct = correct
)

// QuizResult
fun QuizResultEntity.toDomain() = QuizResult(
    id = id,
    quizId = quizId,
    quizTitle = quizTitle,
    userId = userId,
    userName = userName,
    score = score,
    totalQuestions = totalQuestions,
    percentage = percentage,
    timeTakenSeconds = timeTakenSeconds,
    timestamp = timestamp
)

fun QuizResult.toEntity() = QuizResultEntity(
    id = id,
    quizId = quizId,
    quizTitle = quizTitle,
    userId = userId,
    userName = userName,
    score = score,
    totalQuestions = totalQuestions,
    percentage = percentage,
    timeTakenSeconds = timeTakenSeconds,
    timestamp = timestamp
)

// UserProfile
fun UserProfileEntity.toDomain() = UserProfile(
    uid = uid,
    email = email,
    displayName = displayName,
    totalQuizzes = totalQuizzes,
    averageScore = averageScore,
    bestScore = bestScore
)

fun UserProfile.toEntity() = UserProfileEntity(
    uid = uid,
    email = email,
    displayName = displayName,
    totalQuizzes = totalQuizzes,
    averageScore = averageScore,
    bestScore = bestScore
)