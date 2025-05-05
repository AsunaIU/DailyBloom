package com.example.dailybloom.data.source

import com.example.dailybloom.data.remote.HabitMappers.toDomainModel
import com.example.dailybloom.data.remote.HabitMappers.toResponseModel
import com.example.dailybloom.data.remote.HabitApi
import com.example.dailybloom.model.Habit
import kotlinx.coroutines.delay

class RemoteHabitDataSource(private val habitApi: HabitApi) : HabitDataSource {

    override suspend fun getHabits(): Result<List<Habit>> {
        return executeWithRetry {
            val response = habitApi.getHabits()
            response.map { it.toDomainModel() }
        }
    }

    override suspend fun saveHabit(habit: Habit): Result<Habit> {
        return executeWithRetry {
            val response = habitApi.addOrUpdateHabit(habit = habit.toResponseModel())
            response.toDomainModel()
        }
    }

    override suspend fun deleteHabit(habitId: String): Result<Unit> {
        return executeWithRetry {
            habitApi.deleteHabit(habitUid = habitId)
            Unit
        }
    }

    private suspend fun <T> executeWithRetry(
        maxRetries: Int = 3,
        initialDelayMillis: Long = 1000,
        maxDelayMillis: Long = 10000,
        factor: Double = 2.0,
        block: suspend () -> T
    ): Result<T> {
        var currentDelay = initialDelayMillis
        repeat(maxRetries) { attempt ->
            try {
                return Result.success(block())
            } catch (e: Exception) {
                if (attempt == maxRetries - 1) return Result.failure(e)

                delay(currentDelay)
                currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelayMillis)
            }
        }
        return Result.failure(IllegalStateException("Should not reach here"))
    }
}
