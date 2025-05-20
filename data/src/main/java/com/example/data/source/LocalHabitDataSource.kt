package com.example.data.source

import com.example.data.local.HabitDao
import com.example.data.local.HabitEntity
import com.example.domain.model.Habit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalHabitDataSource @Inject constructor (private val habitDao: HabitDao)  {

    // Получение привычек в виде Flow из Room
    fun getHabitsFlow(): Flow<List<Habit>> {
        return habitDao.getAllHabits().map { entities ->
            entities.map { HabitEntity.toHabit(it) }
        }
    }

    suspend fun getHabits(): Result<List<Habit>> = withContext(Dispatchers.IO) {
        try {
            val habits = habitDao.getAllHabits().map { entities ->
                entities.map { HabitEntity.toHabit(it) }
            }.firstOrNull() ?: emptyList()
            Result.success(habits)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addHabit(habit: Habit): Result<Habit> = withContext(Dispatchers.IO) {
        try {
            habitDao.insertHabit(HabitEntity.fromHabit(habit))
            Result.success(habit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateHabit(habitId: String, updatedHabit: Habit): Result<Habit> =
        withContext(Dispatchers.IO) {
            try {
                habitDao.insertHabit(HabitEntity.fromHabit(updatedHabit))
                Result.success(updatedHabit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    suspend fun deleteHabit(habitId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            habitDao.deleteHabit(habitId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setHabitDone(habitId: String, date: Long): Result<Unit> = withContext(Dispatchers.IO) {
            try {
                val habitFlow = habitDao.getHabitById(habitId)
                val habitEntity = habitFlow.firstOrNull()

                if (habitEntity != null) {
                    val habit = HabitEntity.toHabit(habitEntity)
                    val updatedHabit = habit.copy(done = true)
                    habitDao.insertHabit(HabitEntity.fromHabit(updatedHabit))
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Habit not found"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}

