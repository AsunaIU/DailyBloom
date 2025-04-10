package com.example.dailybloom.model

import android.graphics.Color
import android.os.Parcelable
import com.example.dailybloom.R
import com.example.dailybloom.viewmodel.viewmodeldata.UiHabit
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
data class Habit(
    val id: String = UUID.randomUUID().toString(),
    var title: String = "",
    var description: String = "",
    var priority: Priority = Priority.MEDIUM,
    var type: HabitType = HabitType.GOOD,
    var frequency: Int = 1,
    var periodicity: Periodicity = Periodicity.DAY,
    var color: Int = Color.BLUE,
    val createdAt: Long = System.currentTimeMillis(),
) : Parcelable {

    companion object {
        fun toUiHabit(habit: Habit): UiHabit {
            return UiHabit(
                title = habit.title,
                description = habit.description,
                priorityPos = habit.priority.ordinal,
                typeId = if (habit.type == HabitType.GOOD) R.id.rbHabitGood else R.id.rbHabitBad,
                frequency = habit.frequency.toString(),
                periodicityPos = habit.periodicity.ordinal,
                selectedColor = habit.color
            )
        }
    }
}