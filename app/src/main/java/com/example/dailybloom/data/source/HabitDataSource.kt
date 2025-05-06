package com.example.dailybloom.data.source

import com.example.dailybloom.model.Habit

interface HabitDataSource {
    suspend fun getHabits(): Result<List<Habit>>
    suspend fun addHabit(habit: Habit): Result<Habit>
    suspend fun updateHabit(habitId: String, updatedHabit: Habit): Result<Habit>
    suspend fun deleteHabit(habitId: String): Result<Unit>
}