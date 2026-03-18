// Fichier : data/TaskDao.kt
package com.letotoo06.todo.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task) // Create [cite: 8, 50]

    @Update
    suspend fun update(task: Task) // Update [cite: 10, 51]

    @Delete
    suspend fun delete(task: Task) // Delete [cite: 11, 52]

    @Query("SELECT * FROM tasks")
    fun getAll(): Flow<List<Task>> // Read [cite: 9, 53]
}