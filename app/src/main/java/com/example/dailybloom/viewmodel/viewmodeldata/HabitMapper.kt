package com.example.dailybloom.viewmodel.viewmodeldata

import com.example.dailybloom.R
import com.example.domain.model.Habit
import com.example.domain.model.HabitType


/**
 * Mapper class для преобразования между Habit в domain model и presentation model UiHabit
 */

object HabitMapper {
    fun toUiHabit(habit: Habit): UiHabit {
        return UiHabit(
            title = habit.title,
            description = habit.description,
            priorityPos = habit.priority.ordinal,
            typeId = if (habit.type == HabitType.GOOD) R.id.rbHabitGood else R.id.rbHabitBad,
            frequency = habit.frequency.toString(),
            periodicityPos = habit.periodicity.ordinal,
            selectedColor = habit.color,
            done = habit.done
        )
    }
}