package com.example.domain.model

enum class Priority {
    HIGH, MEDIUM, LOW;

    companion object {
        fun fromString(value: String): Priority {
            return when (value.uppercase()) {
                "HIGH" -> HIGH
                "LOW" -> LOW
                else -> MEDIUM
            }
        }

        fun toDisplayString(priority: Priority): String {
            return when (priority) {
                HIGH -> "High"
                MEDIUM -> "Medium"
                LOW -> "Low"
            }
        }
    }
}