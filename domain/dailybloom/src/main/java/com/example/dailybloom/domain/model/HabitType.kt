package com.example.dailybloom.domain.model

enum class HabitType {
    GOOD, BAD;

    companion object {
        fun fromString(value: String): HabitType {
            return when (value.uppercase()) {
                "GOOD" -> GOOD
                else -> BAD
            }
        }

        fun toDisplayString(type: HabitType): String {
            return when (type) {
                GOOD -> "Good"
                BAD -> "Bad"
            }
        }
    }
}