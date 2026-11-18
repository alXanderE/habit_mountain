package com.example.habitmountain.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val MountainColors = darkColorScheme(
    primary = Teal,
    secondary = Gold,
    background = Ink,
    surface = InkSoft,
    onBackground = Snow,
    onSurface = Snow,
    onSurfaceVariant = Mist
)

@Composable
fun HabitMountainTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = MountainColors, content = content)
}
