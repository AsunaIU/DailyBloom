package com.example.dailybloom.data.source

import com.example.dailybloom.domain.model.Habit

interface HabitDataSource {
    suspend fun getHabits(): Result<List<com.example.dailybloom.domain.model.Habit>>
    suspend fun addHabit(habit: com.example.dailybloom.domain.model.Habit): Result<com.example.dailybloom.domain.model.Habit>
    suspend fun updateHabit(habitId: String, updatedHabit: com.example.dailybloom.domain.model.Habit): Result<com.example.dailybloom.domain.model.Habit>
    suspend fun deleteHabit(habitId: String): Result<Unit>
    suspend fun setHabitDone(habitId: String, date: Long = System.currentTimeMillis()): Result<Unit>
}