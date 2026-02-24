package com.example.quizapp.data.remote

import com.example.quizapp.data.local.UserProfileDao
import com.example.quizapp.data.mapper.toEntity
import com.example.quizapp.domain.Result
import com.example.quizapp.domain.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Implementação do repositório de autenticação usando Firebase Auth.
 * Salva o perfil do usuário no Firestore (coleção "users") e localmente via Room.
 */
class AuthRepositoryImpl(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val userProfileDao: UserProfileDao
) : AuthRepository {

    override val currentUser: Flow<String?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            trySend(firebaseAuth.currentUser?.uid)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }

    override fun getUserId(): String? = auth.currentUser?.uid

    override fun getUserEmail(): String? = auth.currentUser?.email

    override fun getUserDisplayName(): String? = auth.currentUser?.displayName

    override suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            // Salvar perfil localmente após login
            val user = auth.currentUser
            if (user != null) {
                val profile = UserProfile(
                    uid = user.uid,
                    email = user.email ?: "",
                    displayName = user.displayName ?: ""
                )
                userProfileDao.insertOrUpdate(profile.toEntity())
            }
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun signUp(email: String, password: String, displayName: String): Result<Unit> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            // Atualizar displayName no Firebase Auth
            val user = auth.currentUser
            user?.updateProfile(userProfileChangeRequest {
                this.displayName = displayName
            })?.await()

            // Salvar perfil no Firestore
            val profile = UserProfile(
                uid = user?.uid ?: "",
                email = email,
                displayName = displayName
            )
            if (user != null) {
                firestore.collection("users")
                    .document(user.uid)
                    .set(
                        hashMapOf(
                            "email" to email,
                            "displayName" to displayName,
                            "totalQuizzes" to 0,
                            "averageScore" to 0.0,
                            "bestScore" to 0.0
                        )
                    ).await()
            }

            // Salvar perfil localmente
            userProfileDao.insertOrUpdate(profile.toEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun logout() {
        auth.signOut()
    }
}
