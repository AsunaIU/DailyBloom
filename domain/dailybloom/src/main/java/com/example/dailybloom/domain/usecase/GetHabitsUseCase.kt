package com.example.dailybloom.domain.usecase

import com.example.dailybloom.domain.model.Habit
import com.example.dailybloom.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetHabitsUseCase @Inject constructor(
    private val habitRepository: HabitRepository
) {
    operator fun invoke(): Flow<Map<String, Habit>> {
        return habitRepository.getHabitsFlow()
    }
}