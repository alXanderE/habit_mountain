package com.example.habitmountain.data

import androidx.compose.ui.graphics.Color

enum class HabitCadence(val label: String) {
    DAILY("Daily"),
    WEEKLY("Weekly"),
    CUSTOM("Custom")
}

data class Habit(
    val id: Long,
    val name: String,
    val cadence: HabitCadence,
    val intervalDays: Int? = null,
    val reward: Int = 10,
    val streak: Int = 0,
    val completed: Boolean = false,
    val accent: Color = Color(0xFF55C6A9)
)
