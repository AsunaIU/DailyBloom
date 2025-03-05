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
    var priority: String = "Medium",
    var type: String = "Good",
    var frequency: Int = 1,
    var periodicity: String = "Day",
    var color: Int = Color.BLUE
) : Parcelable