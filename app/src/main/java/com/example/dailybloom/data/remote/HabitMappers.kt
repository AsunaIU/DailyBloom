package com.example.dailybloom.data.remote

import android.graphics.Color
import com.example.dailybloom.model.Habit
import com.example.dailybloom.model.HabitType
import com.example.dailybloom.model.Periodicity
import com.example.dailybloom.model.Priority
import java.util.UUID

object HabitMappers {
    fun HabitResponse.toDomainModel(): Habit {
        return Habit(
            id = this.uid ?: UUID.randomUUID().toString(),
            title = this.title,
            description = this.description,
            priority = when (this.priority) {
                0 -> Priority.LOW
                1 -> Priority.MEDIUM
                else -> Priority.HIGH
            },
            type = if (this.type == 0) HabitType.GOOD else HabitType.BAD,
            frequency = this.frequency,
            periodicity = Periodicity.DAY, // Default as it's not in API model
            color = this.color ?: Color.BLUE,
            createdAt = this.date
        )
    }

    fun HabitResponse.toUid(): String =
        uid ?: throw IllegalStateException("Server did not return a uid")

    fun Habit.toRequestModel(id: Boolean): HabitResponse {
        val uid = if (id) this.id else null

        return HabitResponse(
            uid = uid,
            title = this.title,
            description = this.description,
            priority = when (this.priority) {
                Priority.LOW -> 0
                Priority.MEDIUM -> 1
                Priority.HIGH -> 2
            },
            type = if (this.type == HabitType.GOOD) 0 else 1,
            frequency = this.frequency,
            count = 1,
            date = this.createdAt,
            color = this.color
        )
    }
}