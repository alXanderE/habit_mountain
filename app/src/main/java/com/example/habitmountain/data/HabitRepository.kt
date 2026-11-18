package com.example.habitmountain.data

import androidx.compose.ui.graphics.Color

/** Local-first domain layer. Replace this with Room/DataStore without changing the UI contract. */
class HabitRepository {
    private var nextId = 4L

    var habits: List<Habit> = listOf(
        Habit(1, "Drink water", HabitCadence.DAILY, reward = 12, streak = 4, completed = true, accent = Color(0xFF59C6DE)),
        Habit(2, "Read 20 pages", HabitCadence.DAILY, reward = 15, streak = 3, accent = Color(0xFFF1B45B)),
        Habit(3, "Morning run", HabitCadence.WEEKLY, reward = 25, streak = 8, accent = Color(0xFFCB7FEC))
    )
        private set

    var coins: Int = 128
        private set

    fun toggle(habitId: Long) {
        habits = habits.map { habit ->
            if (habit.id != habitId) habit
            else if (habit.completed) {
                coins = (coins - habit.reward).coerceAtLeast(0)
                habit.copy(completed = false, streak = (habit.streak - 1).coerceAtLeast(0))
            }
            else {
                coins += habit.reward
                habit.copy(completed = true, streak = habit.streak + 1)
            }
        }
    }

    fun add(name: String, cadence: HabitCadence, intervalDays: Int? = null) {
        habits = habits + Habit(nextId++, name.trim(), cadence, intervalDays = intervalDays, accent = Color(0xFF55C6A9))
    }

    val completedToday: Int get() = habits.count { it.completed }
}
