package com.example.dailybloom.model

import android.graphics.Color
import android.os.Parcelable
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
    var color: Int = Color.BLUE
) : Parcelable