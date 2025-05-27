package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.domain.model.Habit
import com.example.domain.model.HabitType
import com.example.domain.model.Periodicity
import com.example.domain.model.Priority
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromLongList(value: List<Long>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toLongList(value: String): List<Long> {
        return try {
            Json.decodeFromString(value)
        } catch (e: Exception) {
            emptyList()
        }
    }
}

@Entity(tableName = "habits")
@TypeConverters(Converters::class)
data class HabitEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val priorityName: String,
    val typeName: String,
    val frequency: Int,
    val periodicityName: String,
    val color: Int,
    val createdAt: Long,
    val doneDates: List<Long> = emptyList()
) {

    companion object {
        fun fromHabit(habit: Habit): HabitEntity {
            return HabitEntity(
                id = habit.id,
                title = habit.title,
                description = habit.description,
                priorityName = habit.priority.name,
                typeName = habit.type.name,
                frequency = habit.frequency,
                periodicityName = habit.periodicity.name,
                color = habit.color,
                createdAt = habit.createdAt,
                doneDates = habit.doneDates
            )
        }

        fun toHabit(entity: HabitEntity): Habit {
            return Habit(
                id = entity.id,
                title = entity.title,
                description = entity.description,
                priority = Priority.fromString(entity.priorityName),
                type = HabitType.fromString(entity.typeName),
                frequency = entity.frequency,
                periodicity = Periodicity.fromString(entity.periodicityName),
                color = entity.color,
                createdAt = entity.createdAt,
                doneDates = entity.doneDates
            )
        }
    }
}