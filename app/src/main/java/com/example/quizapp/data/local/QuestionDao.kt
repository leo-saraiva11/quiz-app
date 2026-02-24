package com.example.quizapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.quizapp.data.local.entity.QuestionEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO de acesso às questões armazenadas localmente.
 */
@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions WHERE quizId = :quizId")
    fun getQuestionsByQuiz(quizId: String): Flow<List<QuestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<QuestionEntity>)

    @Query("DELETE FROM questions WHERE quizId = :quizId")
    suspend fun deleteByQuiz(quizId: String)

    @Query("DELETE FROM questions")
    suspend fun deleteAll()
}
