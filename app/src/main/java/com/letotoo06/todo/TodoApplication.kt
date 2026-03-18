package com.letotoo06.todo

import android.app.Application
import androidx.room.Room
import com.letotoo06.todo.data.AppDatabase

class TodoApplication : Application() {
    val database: AppDatabase by lazy {
        Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "todo_database"
        )
            .fallbackToDestructiveMigration() // 👈 NOUVEAU : Réinitialise proprement la base
            .build()
    }
}