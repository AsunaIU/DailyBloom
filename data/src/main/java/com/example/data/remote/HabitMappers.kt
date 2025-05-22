package com.example.data.remote

import android.graphics.Color
import com.example.domain.model.Habit
import com.example.domain.model.HabitType
import com.example.domain.model.Periodicity
import com.example.domain.model.Priority
import java.util.UUID


object HabitMappers {

    // Преобразует сетевую модель в доменную
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
            periodicity = Periodicity.DAY, // по умолчанию, так как его нет в модели API
            color = this.color ?: Color.BLUE,
            createdAt = this.date,
            done = this.doneDates.isNotEmpty(),  // будет отмечено как выполненное, если в списке doneDates есть какие-либо даты
        )
    }

    // Получает uid из ответа
    fun HabitResponse.toUid(): String =
        uid ?: throw IllegalStateException("Server did not return a uid")

    // Преобразует доменную модель в сетевую для отправки на сервер
    fun Habit.toRequestModel(id: Boolean): HabitResponse {
        val uid = if (id) this.id else null

        return HabitResponse(
            uid = uid,
            title = this.title,
            description = this.description.ifEmpty { " " },
            priority = when (this.priority) {
                Priority.LOW -> 0
                Priority.MEDIUM -> 1
                Priority.HIGH -> 2
            },
            type = if (this.type == HabitType.GOOD) 0 else 1,
            frequency = this.frequency,
            count = 1,
            date = this.createdAt,
            color = this.color,
            doneDates = emptyList(), // пустые,т.к. управляются сервером
        )
    }
}