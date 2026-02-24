package com.example.quizapp

import android.app.Application
import com.example.quizapp.data.AppContainer

/**
 * Application class que inicializa o AppContainer (Room Database).
 * Registrada no AndroidManifest como android:name=".QuizApplication".
 */
class QuizApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContainer.init(this)
    }
}
