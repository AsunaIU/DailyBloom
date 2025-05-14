package com.example.dailybloom.data.local

import android.util.Log
import com.example.dailybloom.data.source.HabitDataSource
import com.example.dailybloom.data.source.LocalHabitDataSource
import com.example.dailybloom.model.Habit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


class HabitRepositoryImpl(
    private val localDataSource: HabitDataSource,
    private val remoteDataSource: HabitDataSource,
    private val appScope: CoroutineScope
) {

    val habits: Flow<Map<String, Habit>> = (localDataSource as LocalHabitDataSource)
        .getHabitsFlow()
        .map { list -> list.associateBy { it.id } }

    init {
        Log.d("HabitRepositoryImpl", "Initializing repository")
        appScope.launch {
            Log.d("HabitRepositoryImpl", "Starting initial habits refresh")
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
            } else {
                Log.d("HabitRepositoryImpl", "Failed to refresh habits from remote, trying locally")
            }
        } catch (ce: kotlinx.coroutines.CancellationException) {
            throw ce
        } catch (e: Exception) {
            Log.e("HabitRepositoryImpl", "Exception while refrehing habits", e)
        }
    }

    suspend fun addHabit(habit: Habit): Boolean {
        try {
            val remoteResult = remoteDataSource.addHabit(habit)
            Log.d("api", "remote add status ${remoteResult.isSuccess}")
            if (remoteResult.isSuccess) {
                Log.d("api", "remote successfull")
                val remoteHabit = remoteResult.getOrNull()
                if (remoteHabit != null) {
                    localDataSource.addHabit(remoteHabit)
                    Log.d("api", "local successfull")
                } else {
                    throw Exception("Failed to save to remote: server return null")
                }
            } else {
                throw Exception("Failed to save to remote: ${remoteResult.exceptionOrNull()?.message}")
            }

            return true
        } catch (ce: kotlinx.coroutines.CancellationException) {
            throw ce
        } catch (e: Exception) {
            Log.e("HabitRepositoryImpl", "Exception while saving habit: ${e.message}", e)
            return false
        }
    }


    suspend fun updateHabit(habitId: String, updatedHabit: Habit): Boolean {
        try {
            val remoteResult = remoteDataSource.updateHabit(habitId, updatedHabit)
            if (remoteResult.isSuccess) {
                val remoteUpdatedHabit = remoteResult.getOrNull()
                if (remoteUpdatedHabit != null) {
                    localDataSource.updateHabit(habitId, remoteUpdatedHabit)

                } else {
                    throw Exception("Failed to save to remote: server return null")
                }
            } else {
                throw Exception("Failed to save to remote: ${remoteResult.exceptionOrNull()?.message}")
            }

            return true
        } catch (ce: kotlinx.coroutines.CancellationException) {
            throw ce
        } catch (e: Exception) {
            Log.e("HabitRepositoryImpl", "Exception while updating habit: ${e.message}", e)
            return false
        }
    }

    suspend fun removeHabit(habitId: String): Boolean {
        try {
            val remoteResult = remoteDataSource.deleteHabit(habitId)
            if (remoteResult.isSuccess) {
                val localResult = localDataSource.deleteHabit(habitId)
                if (localResult.isSuccess) {
                    return true
                } else {
                    throw Exception("Failed to delete habit locally")
                }
            } else {
                throw Exception("Failed to delete habit on remote")
            }
        } catch (ce: kotlinx.coroutines.CancellationException) {
            throw ce
        } catch (e: Exception) {
            Log.e("HabitRepositoryImpl", "Exception while deleting habit: ${e.message}", e)
            return false
        }
    }

    suspend fun getHabits(): Map<String, Habit> {
        val result = localDataSource.getHabits()
        return result.getOrNull()?.associateBy { it.id } ?: emptyMap()
    }

    fun syncWithServer() {
        appScope.launch {
            refreshHabits()
        }
    }
}