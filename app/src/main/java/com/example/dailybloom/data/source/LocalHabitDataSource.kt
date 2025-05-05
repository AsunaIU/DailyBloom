package com.example.dailybloom.data.source

import com.example.dailybloom.model.Habit
import com.example.dailybloom.data.local.HabitDao
import com.example.dailybloom.data.local.HabitEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalHabitDataSource(private val habitDao: HabitDao) : HabitDataSource {

    override suspend fun getHabits(): Result<List<Habit>> = withContext(Dispatchers.IO) {
        try {
            val habits = habitDao.getAllHabitsSync().map { HabitEntity.toHabit(it) }
            Result.success(habits)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun saveHabit(habit: Habit): Result<Habit> = withContext(Dispatchers.IO) {
        try {
            habitDao.insertHabit(HabitEntity.fromHabit(habit))
            Result.success(habit)
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
