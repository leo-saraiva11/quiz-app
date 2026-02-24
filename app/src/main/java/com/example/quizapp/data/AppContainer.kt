package com.example.quizapp.data

import android.content.Context
import com.example.quizapp.data.local.AppDatabase
import com.example.quizapp.data.remote.AuthRepository
import com.example.quizapp.data.remote.AuthRepositoryImpl
import com.example.quizapp.data.remote.QuizRepository
import com.example.quizapp.data.remote.QuizRepositoryImpl
import com.example.quizapp.data.remote.ResultRepository
import com.example.quizapp.data.remote.ResultRepositoryImpl
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
