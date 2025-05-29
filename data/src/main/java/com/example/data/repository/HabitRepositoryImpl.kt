package com.example.data.repository

import android.util.Log
import com.example.data.source.LocalHabitDataSource
import com.example.data.source.RemoteHabitDataSource
import com.example.domain.model.Habit
import com.example.domain.repository.HabitRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HabitRepositoryImpl @Inject constructor(
    private val localDataSource: LocalHabitDataSource,
    private val remoteDataSource: RemoteHabitDataSource,
    private val appScope: CoroutineScope
) : HabitRepository {

    companion object {
        private const val TAG = "HabitRepository"
    }

    init {
        appScope.launch {
            refreshHabits()
        }
    }

    private suspend fun refreshHabits() {
        try {
            val remoteResult = remoteDataSource.getHabits()
            if (remoteResult.isSuccess) {
                val remoteHabits = remoteResult.getOrNull() ?: emptyList()
                for (habit in remoteHabits) {
                    localDataSource.addHabit(habit)
                }
            }
        } catch (ce: kotlinx.coroutines.CancellationException) {
            throw ce
        } catch (e: Exception) {
            Log.e(TAG, "Exception while refreshing habits", e)
        }
    }

    override fun getHabitsFlow(): Flow<Map<String, Habit>> {
        return localDataSource.getHabitsFlow()
            .map { list -> list.associateBy { it.id } }
    }

    override suspend fun getHabits(): Result<Map<String, Habit>> {
        return try {
            val result = localDataSource.getHabits()
            val habits = result.getOrNull()?.associateBy { it.id } ?: emptyMap()
            Result.success(habits)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addHabit(habit: Habit): Result<Boolean> {
        return try {
            val remoteResult = remoteDataSource.addHabit(habit)
            if (remoteResult.isSuccess) {
                val remoteHabit = remoteResult.getOrNull()
                if (remoteHabit != null) {
                    localDataSource.addHabit(remoteHabit)
                    Result.success(true)
                } else {
                    throw Exception("Failed to save to remote: server returned null")
                }
            } else {
                throw Exception("Failed to save to remote: ${remoteResult.exceptionOrNull()?.message}")
            }
        } catch (ce: kotlinx.coroutines.CancellationException) {
            throw ce
        } catch (e: Exception) {
            Log.e(TAG, "Exception while saving habit: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun updateHabit(habitId: String, updatedHabit: Habit): Result<Boolean> {
        return try {
            val remoteResult = remoteDataSource.updateHabit(habitId, updatedHabit)
            if (remoteResult.isSuccess) {
                val remoteUpdatedHabit = remoteResult.getOrNull()
                if (remoteUpdatedHabit != null) {
                    localDataSource.updateHabit(habitId, remoteUpdatedHabit)
                    Result.success(true)
                } else {
                    throw Exception("Failed to save to remote: server returned null")
                }
            } else {
                throw Exception("Failed to save to remote: ${remoteResult.exceptionOrNull()?.message}")
            }
        } catch (ce: kotlinx.coroutines.CancellationException) {
            throw ce
        } catch (e: Exception) {
            Log.e(TAG, "Exception while updating habit: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun removeHabit(habitId: String): Result<Boolean> {
        return try {
            val remoteResult = remoteDataSource.deleteHabit(habitId)
            if (remoteResult.isSuccess) {
                val localResult = localDataSource.deleteHabit(habitId)
                if (localResult.isSuccess) {
                    Result.success(true)
                } else {
                    throw Exception("Failed to delete habit locally")
                }
            } else {
                throw Exception("Failed to delete habit on remote")
            }
        } catch (ce: kotlinx.coroutines.CancellationException) {
            throw ce
        } catch (e: Exception) {
            Log.e(TAG, "Exception while deleting habit: ${e.message}", e)
            Result.failure(e)
        }
    }

    override suspend fun setHabitDone(habitId: String, date: Long): Result<Boolean> {
        return try {
            val remoteResult = remoteDataSource.setHabitDone(habitId, date)
            if (remoteResult.isSuccess) {
                val localResult = localDataSource.setHabitDone(habitId, date)
                if (localResult.isSuccess) {
                    refreshHabits()
                    Result.success(true)
                } else {
                    throw Exception("Failed to mark habit as done locally")
                }
            } else {
                throw Exception("Failed to mark habit as done on remote")
            }
        } catch (ce: kotlinx.coroutines.CancellationException) {
            throw ce
        } catch (e: Exception) {
            Log.e(TAG, "Exception while marking habit as done: ${e.message}", e)
            Result.failure(e)
        }
    }

    override fun syncWithServer() {
        appScope.launch {
            refreshHabits()
        }
    }
}