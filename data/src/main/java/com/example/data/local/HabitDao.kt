package com.example.data.local

import androidx.room.Dao
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Insert
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    // Room + LiveData = Room выполняет запрос (чтения из БД) в фоновом потоке

    @Query("SELECT * FROM habits")
    fun getAllHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE id = :habitId")
    fun getHabitById(habitId: String): Flow<HabitEntity?>

    // Room + suspend = Room сам переключает выполнение (записи в БД) в фоновый поток

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity)

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Query("DELETE FROM habits WHERE id = :habitId")
    suspend fun deleteHabit(habitId: String)
}