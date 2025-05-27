package com.example.domain.model

enum class Periodicity {
    DAY, WEEK, MONTH;

    companion object {
        fun fromString(value: String): Periodicity {
            return when (value.uppercase()) {
                "DAY" -> DAY
                "WEEK" -> WEEK
                "MONTH" -> MONTH
                else -> DAY
            }
        }

        fun toDisplayString(periodicity: Periodicity): String {
            return when (periodicity) {
                DAY -> "Day"
                WEEK -> "Week"
                MONTH -> "Month"
            }
        }
    }
}