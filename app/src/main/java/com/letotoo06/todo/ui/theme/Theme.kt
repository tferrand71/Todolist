package com.letotoo06.todo.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Palette pour le mode Sombre
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryDark,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = DarkBackground,
    onBackground = TextLight,
    onSurface = TextLight
)

// Palette pour le mode Clair (Crème)
private val LightColorScheme = lightColorScheme(
    primary = PrimaryCream,
    background = CreamBackground,
    surface = CreamSurface,
    onPrimary = CreamSurface,
    onBackground = TextDark,
    onSurface = TextDark
)

@Composable
fun ToDoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // Détecte automatiquement le mode du téléphone
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    // Ça colore la barre tout en haut du téléphone (où il y a l'heure et la batterie)
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Laisse par défaut si tu as un fichier Type.kt
        content = content
    )
}