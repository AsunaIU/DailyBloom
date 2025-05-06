package com.example.dailybloom.data.local

import android.util.Log
import com.example.dailybloom.data.source.HabitDataSource
import com.example.dailybloom.model.Habit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class HabitRepositoryImpl(
    private val localDataSource: HabitDataSource,
    private val remoteDataSource: HabitDataSource,
    private val appScope: CoroutineScope
) {

    private val _habits = MutableStateFlow<Map<String, Habit>>(emptyMap())
    val habits: StateFlow<Map<String, Habit>> = _habits.asStateFlow()

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

            val localResult = localDataSource.getHabits()
            if (localResult.isSuccess) {
                val habitMap = localResult.getOrNull()?.associateBy { it.id } ?: emptyMap()
                _habits.value = habitMap
            } else {
                throw Exception("Failed to fetch habit locally")
            }
        } catch (ce: kotlinx.coroutines.CancellationException) {
            throw ce
        } catch (e: Exception) {
            Log.e("HabitRepositoryImpl", "Exception while refrehing habits", e)
        }
    }

    suspend fun addHabit(habit: Habit): Boolean {
        try {
            Log.d("api", "sosat!!!!!!!")
            val remoteResult = remoteDataSource.addHabit(habit)
            Log.d("api", "remote add status ${remoteResult.isSuccess}")
            if (remoteResult.isSuccess) {
                Log.d("api", "remote successfull")
                val remoteHabit = remoteResult.getOrNull()
                if (remoteHabit != null) {
                    localDataSource.addHabit(remoteHabit)
                    Log.d("api", "local successfull")

                    val currentMap = _habits.value.toMutableMap()
                    currentMap[remoteHabit.id] = remoteHabit
                    _habits.value = currentMap

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

                    val currentMap = _habits.value.toMutableMap()
                    currentMap[habitId] = remoteUpdatedHabit
                    _habits.value = currentMap

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
                    val currentMap = _habits.value.toMutableMap()
                    currentMap.remove(habitId)
                    _habits.value = currentMap
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

    fun getHabits(): Map<String, Habit> = _habits.value

    fun syncWithServer() {
        appScope.launch {
            refreshHabits()
        }
    }
}