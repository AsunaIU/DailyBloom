package com.example.dailybloom.data.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.example.dailybloom.model.Habit
import java.lang.ref.WeakReference
import java.util.concurrent.CopyOnWriteArrayList
import androidx.lifecycle.Observer
import androidx.lifecycle.ProcessLifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// управляет хранилищем привычек (habits) и уведомляет listeners об изменениях
// работает как менеджер данных, предоставляя интерфейс для добавления, обновления, удаления и получения списка привычек

object HabitRepository {

    private lateinit var database: HabitDatabase

    private val _habits = MutableLiveData<Map<String, Habit>>()
    val habits: LiveData<Map<String, Habit>> = _habits

    private var habitsObserver: Observer<Map<String, Habit>>? = null

    fun initialize(appDatabase: HabitDatabase) {
        database = appDatabase

        val sourceLiveData = database.habitDao().getAllHabits()

        val transformedLiveData = sourceLiveData.map { habitEntities ->
            habitEntities.associate { entity ->
                val habit = HabitEntity.toHabit(entity) // преобразуем HabitEntity в Habit - «значение»
                habit.id to habit // создаём пару (ключ → значение): id привычки — «ключ», объект Habit — «значение»
            }
        }

        habitsObserver = Observer { habitMap -> // observer, при каждом изменении данных: обновляет _habits, оповещает слушателей
            _habits.value = habitMap
        }

        transformedLiveData.observe(      // подписываем Observer на transformedLiveData
            ProcessLifecycleOwner.get(),  // используется ProcessLifecycleOwner, чтобы слушать, пока живо приложение
            habitsObserver!!
        )
    }


    // Корутины с suspend-функциями для возврата результатов

    suspend fun addHabit(habit: Habit): Boolean {
        return try {
            val habitEntity = HabitEntity.fromHabit(habit)
            withContext(Dispatchers.IO) {
                database.habitDao().insertHabit(habitEntity)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateHabit(habitId: String, updatedHabit: Habit): Boolean {
        return try {
            val habitToUpdate = updatedHabit.copy(id = habitId)
            val habitEntity = HabitEntity.fromHabit(habitToUpdate)
            withContext(Dispatchers.IO) {
                database.habitDao().updateHabit(habitEntity)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun removeHabit(habitId: String): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                database.habitDao().deleteHabit(habitId)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getHabits(): Map<String, Habit> = _habits.value ?: emptyMap()
}
