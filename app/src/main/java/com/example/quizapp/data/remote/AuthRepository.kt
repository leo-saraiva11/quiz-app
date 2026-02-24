package com.example.quizapp.data.remote

import com.example.quizapp.domain.Result
import kotlinx.coroutines.flow.Flow

/**
 * Interface do repositório de autenticação.
 * Provê operações de login, cadastro e logout via Firebase Auth,
 * além de manter o perfil do usuário localmente.
 */
interface AuthRepository {
    /** Fluxo do ID do usuário logado, ou null se ninguém estiver logado. */
    val currentUser: Flow<String?>

    /** Retorna o ID do usuário logado sincronamente, ou null. */
    fun getUserId(): String?

    /** Retorna o email do usuário logado, ou null. */
    fun getUserEmail(): String?

    /** Retorna o nome de exibição do usuário logado, ou null. */
    fun getUserDisplayName(): String?

    /** Realiza login com email e senha. */
    suspend fun login(email: String, password: String): Result<Unit>

    /** Cria nova conta com email, senha e nome de exibição. */
    suspend fun signUp(email: String, password: String, displayName: String): Result<Unit>

    /** Realiza logout e limpa dados locais do perfil. */
    fun logout()
}
