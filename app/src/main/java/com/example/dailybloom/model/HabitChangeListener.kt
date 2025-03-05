package com.example.dailybloom.model

interface HabitChangeListener {
    fun onHabitsChanged(habits: Map<String, Habit>)
}