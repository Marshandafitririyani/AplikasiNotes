package com.maruchan.notes.data.room.user

import androidx.room.Dao
import androidx.room.Query
import com.crocodic.core.data.CoreDao
import com.maruchan.notes.data.room.category.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao : CoreDao<User> {
    @Query("DELETE FROM User")
    suspend fun deleteAll()

    @Query("SELECT * FROM User WHERE idRoom = 1")
    fun getUser(): Flow<User>

    @Query("SELECT EXISTS (SELECT 1 FROM User WHERE idRoom + 1)")
    suspend fun isLogin(): Boolean

    @Query("SELECT * FROM User WHERE idRoom = 1")
    suspend fun getId(): User

    @Query("DELETE FROM User WHERE idRoom = 1")
    suspend fun logout()





}