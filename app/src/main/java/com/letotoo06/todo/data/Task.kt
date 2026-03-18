package com.letotoo06.todo.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    var titre: String = "",
    var description: String = "",
    var estTerminee: Boolean = false,
    var dateLimite: Long? = null // 👈 NOUVEAU : La date limite (peut être nulle)
)