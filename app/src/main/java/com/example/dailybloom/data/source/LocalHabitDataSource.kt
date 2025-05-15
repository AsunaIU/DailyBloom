package com.example.dailybloom.data.source

import com.example.dailybloom.domain.model.Habit
import com.example.dailybloom.data.local.HabitDao
import com.example.dailybloom.data.local.HabitEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class LocalHabitDataSource(private val habitDao: com.example.dailybloom.data.local.HabitDao) : HabitDataSource {

    // Получение привычек в виде Flow из Room
    fun getHabitsFlow(): Flow<List<com.example.dailybloom.domain.model.Habit>> {
        return habitDao.getAllHabits().map { entities ->
            entities.map { com.example.dailybloom.data.local.HabitEntity.toHabit(it) }
        }
    }

    override suspend fun getHabits(): Result<List<com.example.dailybloom.domain.model.Habit>> = withContext(Dispatchers.IO) {
        try {
            val habits = habitDao.getAllHabits().map { entities ->
                entities.map { com.example.dailybloom.data.local.HabitEntity.toHabit(it) }
            }.firstOrNull() ?: emptyList()
            Result.success(habits)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addHabit(habit: com.example.dailybloom.domain.model.Habit): Result<com.example.dailybloom.domain.model.Habit> = withContext(Dispatchers.IO) {
        try {
            habitDao.insertHabit(com.example.dailybloom.data.local.HabitEntity.fromHabit(habit))
            Result.success(habit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateHabit(habitId: String, updatedHabit: com.example.dailybloom.domain.model.Habit): Result<com.example.dailybloom.domain.model.Habit> =
        withContext(Dispatchers.IO) {
            try {
                habitDao.insertHabit(com.example.dailybloom.data.local.HabitEntity.fromHabit(updatedHabit))
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

    override suspend fun setHabitDone(habitId: String, date: Long): Result<Unit> = withContext(Dispatchers.IO) {
            try {
                val habitFlow = habitDao.getHabitById(habitId)
                val habitEntity = habitFlow.firstOrNull()

                if (habitEntity != null) {
                    val habit = com.example.dailybloom.data.local.HabitEntity.toHabit(habitEntity)
                    val updatedHabit = habit.copy(done = true)
                    habitDao.insertHabit(com.example.dailybloom.data.local.HabitEntity.fromHabit(updatedHabit))
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Habit not found"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}

