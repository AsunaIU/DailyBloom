package com.example.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// data model for the API response

@Serializable
data class HabitResponse (
    @SerialName("uid") val uid: String? = null,
    @SerialName("title") val title: String,
    @SerialName("description") val description: String,
    @SerialName("priority") val priority: Int,            // 0 - low, 1 - medium, 2 - high
    @SerialName("type") val type: Int,                    // 0 - good, 1 - bad
    @SerialName("frequency") val frequency: Int,
    @SerialName("count") val count: Int = 0,
    @SerialName("date") val date: Long = System.currentTimeMillis(),
    @SerialName("color") val color: Int? = null,
    @SerialName("done_dates") val doneDates: List<Long> = emptyList(),
)

@Serializable
data class UidResponse(
    @SerialName("uid") val uid: String
)