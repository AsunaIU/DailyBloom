package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.Habit
import com.example.domain.model.HabitType
import com.example.domain.model.Periodicity
import com.example.domain.model.Priority

@Entity(tableName = "habits")
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
    val done: Boolean,
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
                done = habit.done
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
                done = entity.done
            )
        }
    }
}