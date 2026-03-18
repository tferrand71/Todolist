package com.letotoo06.todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

import com.letotoo06.todo.ui.TodoViewModel
import com.letotoo06.todo.ui.TodoViewModelFactory
import com.letotoo06.todo.ui.screens.MainScreen
import com.letotoo06.todo.ui.theme.ToDoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val app = application as TodoApplication
            val dao = app.database.taskDao()
            val viewModel: TodoViewModel = viewModel(
                factory = TodoViewModelFactory(dao)
            )

            // NOUVEAU : On écoute la variable du thème en temps réel !
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()

            // NOUVEAU : On force le thème selon le choix du ViewModel
            ToDoTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(viewModel = viewModel)
                }
            }
        }
    }
}