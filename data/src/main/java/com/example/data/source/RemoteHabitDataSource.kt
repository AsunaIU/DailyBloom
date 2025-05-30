package com.example.data.source

import android.util.Log
import com.example.data.remote.HabitApi
import com.example.data.remote.HabitDoneRequest
import com.example.data.remote.HabitMappers.toDomainModel
import com.example.data.remote.HabitMappers.toRequestModel
import com.example.data.remote.UidResponse
import com.example.domain.model.Habit
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteHabitDataSource @Inject constructor (private val habitApi: HabitApi) {

    private val TAG = "RemoteHabitDataSource"

    suspend fun getHabits(): Result<List<Habit>> {
        return executeWithRetry {
            val response = habitApi.getHabits()
            response.map { it.toDomainModel() }
        }
    }

    suspend fun addHabit(habit: Habit): Result<Habit> {
        return executeWithRetry {
            val request = habit.toRequestModel(id = false)
            val response = habitApi.addOrUpdateHabit(habit = request)
            val newUid = response.uid
            habit.copy(id = newUid)
        }
    }

    suspend fun updateHabit(habitId: String, updatedHabit: Habit): Result<Habit> {
        return executeWithRetry {
            val request = updatedHabit.toRequestModel(id = true)
            Log.d(TAG, "Sending habit to API: ${request.uid}")
            val response = habitApi.addOrUpdateHabit(habit = request)
            val newUid = response.uid
            updatedHabit.copy(id = newUid)
        }
    }

    suspend fun deleteHabit(habitId: String): Result<Unit> {
        return executeWithRetry {
            habitApi.deleteHabit(uid = UidResponse(habitId))
        }
    }

    suspend fun setHabitDone(habitId: String, date: Long): Result<Unit> {
        return executeWithRetry {
            Log.d(TAG, "Setting habit as done: $habitId, date: $date")
            val request = HabitDoneRequest(uid = habitId, date = date)
            val response = habitApi.setHabitDone(habitDone = request)
            Log.d(TAG, "Habit marked as done, response: ${response.code()}")
            Unit
        }
    }

    private suspend fun <T> executeWithRetry(
        maxRetries: Int = 3,
        initialDelayMillis: Long = 1000,
        maxDelayMillis: Long = 15000,
        factor: Double = 2.0,
        timeoutMillis: Long = 25000,
        block: suspend () -> T
    ): Result<T> {
        var currentDelay = initialDelayMillis
        var lastException: Exception? = null

        repeat(maxRetries) { attempt ->
            try {
                return Result.success(withTimeout(timeoutMillis) { block() })
            } catch (e: CancellationException) {
                Log.e(TAG, "Operation canceled (not retrying, propagating cancellation)", e)
                throw e
            } catch (e: Exception) {
                lastException = e
                Log.e(TAG, "API call failed (attempt ${attempt+1}/$maxRetries): ${e.message}", e)

                if (attempt == maxRetries - 1) {
                    Log.e(TAG, "All retry attempts failed", e)
                    return Result.failure(e)
                }

                Log.d(TAG, "Retrying in ${currentDelay}ms...")
                delay(currentDelay)
                currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelayMillis)
            }
        }

        return Result.failure(lastException ?: IllegalStateException("Unknown error during retry"))
    }
}