package com.example.quizapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.quizapp.data.local.entity.UserProfileEntity

/**
 * DAO de acesso ao perfil do usuário armazenado localmente.
 */
@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profiles WHERE uid = :uid")
    suspend fun getProfile(uid: String): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(profile: UserProfileEntity)

    @Query("DELETE FROM user_profiles WHERE uid = :uid")
    suspend fun delete(uid: String)
}
