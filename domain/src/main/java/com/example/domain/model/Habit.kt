package com.example.domain.model

import java.util.UUID

data class Habit(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val priority: Priority,
    val type: HabitType,
    val frequency: Int,
    val periodicity: Periodicity,
    val color: Int,
    val createdAt: Long = System.currentTimeMillis(),
    val done: Boolean = false
) {

//    companion object {
//        fun toUiHabit(habit: Habit): UiHabit {
//            return UiHabit(
//                title = habit.title,
//                description = habit.description,
//                priorityPos = habit.priority.ordinal,
//                typeId = if (habit.type == HabitType.GOOD) R.id.rbHabitGood else R.id.rbHabitBad,
//                frequency = habit.frequency.toString(),
//                periodicityPos = habit.periodicity.ordinal,
//                selectedColor = habit.color,
//                done = habit.done
//            )
//        }
//    }
}