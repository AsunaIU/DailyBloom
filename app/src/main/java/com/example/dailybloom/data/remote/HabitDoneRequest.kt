package com.example.dailybloom.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HabitDoneRequest(
    @SerialName("uid") val uid: String,
    @SerialName("date") val date: Long = System.currentTimeMillis()
)