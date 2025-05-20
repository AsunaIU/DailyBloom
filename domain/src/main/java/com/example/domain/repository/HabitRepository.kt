package com.example.domain.repository

import com.example.domain.model.Habit
import kotlinx.coroutines.flow.Flow

interface HabitRepository {

    fun getHabitsFlow(): Flow<Map<String, Habit>>
    suspend fun getHabits(): Result<Map<String, Habit>>
    suspend fun addHabit(habit: Habit): Result<Boolean>
    suspend fun updateHabit(habitId: String, updatedHabit: Habit): Result<Boolean>
    suspend fun removeHabit(habitId: String): Result<Boolean>
    suspend fun setHabitDone(habitId: String): Result<Boolean>
    fun syncWithServer()
}