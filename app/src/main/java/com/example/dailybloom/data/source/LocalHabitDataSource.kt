package com.example.dailybloom.data.source

import com.example.dailybloom.model.Habit
import com.example.dailybloom.data.local.HabitDao
import com.example.dailybloom.data.local.HabitEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class LocalHabitDataSource(private val habitDao: HabitDao) : HabitDataSource {

    // Получение привычек в виде Flow из Room
    fun getHabitsFlow(): Flow<List<Habit>> {
        return habitDao.getAllHabits().map { entities ->
            entities.map { HabitEntity.toHabit(it) }
        }
    }

    override suspend fun getHabits(): Result<List<Habit>> = withContext(Dispatchers.IO) {
        try {
            val habits = habitDao.getAllHabits().map { entities ->
                entities.map { HabitEntity.toHabit(it) }
            }.firstOrNull() ?: emptyList()
            Result.success(habits)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addHabit(habit: Habit): Result<Habit> = withContext(Dispatchers.IO) {
        try {
            habitDao.insertHabit(HabitEntity.fromHabit(habit))
            Result.success(habit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateHabit(habitId: String, updatedHabit: Habit): Result<Habit> = withContext(Dispatchers.IO) {
        try {
            habitDao.insertHabit(HabitEntity.fromHabit(updatedHabit))
            Result.success(updatedHabit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteHabit(habitId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            habitDao.deleteHabit(habitId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
