package com.letotoo06.todo.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Task::class], version = 2, exportSchema = false) // 👈 Version 2
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}