package com.example.dailybloom.domain.usecase

import com.example.dailybloom.domain.model.Habit
import com.example.dailybloom.domain.repository.HabitRepository
import javax.inject.Inject

class AddHabitUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    suspend operator fun invoke(habit: Habit): Result<Boolean> {
        return habitRepository.addHabit(habit)
    }
}
