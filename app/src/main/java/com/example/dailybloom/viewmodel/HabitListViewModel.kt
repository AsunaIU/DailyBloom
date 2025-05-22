package com.example.dailybloom.viewmodel

import android.util.Log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dailybloom.viewmodel.viewmodeldata.FilterCriteria
import com.example.dailybloom.viewmodel.viewmodeldata.SortOption
import com.example.domain.model.Habit
import com.example.domain.usecase.GetHabitsUseCase
import com.example.domain.usecase.UpdateHabitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HabitListViewModel @Inject constructor(
    private val getHabitsUseCase: GetHabitsUseCase,
    private val updateHabitUseCase: UpdateHabitUseCase
) : ViewModel() {

    private val TAG = HabitListViewModel::class.java.simpleName

    private val _habits = MutableStateFlow<Map<String, Habit>>(emptyMap())
    val habits: StateFlow<Map<String, Habit>> = _habits.asStateFlow()

    private val _filterCriteria = MutableStateFlow(FilterCriteria())
    val filterCriteria: StateFlow<FilterCriteria> = _filterCriteria.asStateFlow()

    private val _operationStatus = MutableStateFlow<OperationStatus?>(null)
    val operationStatus: StateFlow<OperationStatus?> = _operationStatus.asStateFlow()

    val filteredHabits: StateFlow<List<Habit>> = combine(
        _habits,
        _filterCriteria
    ) { habitsMap, criteria ->
        val list = habitsMap.values.toList()
        Log.d(TAG, "Applying filters on ${list.size} habits with criteria=$criteria")
        val result = applyFilters(list, criteria)
        Log.d(TAG, "Filtered result contains ${result.size} habits")
        result
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        loadHabits()
    }

    private fun loadHabits() {
        viewModelScope.launch {
            getHabitsUseCase().collect { habitsMap ->
                Log.d(TAG, "Repository habits changed: ${habitsMap.size} items")
                _habits.value = habitsMap
            }
        }
    }

    fun toggleSortDirection() {
        _filterCriteria.value = _filterCriteria.value.copy(
            ascending = !_filterCriteria.value.ascending
        )
    }

    fun updateFilters(criteria: FilterCriteria) {
        _filterCriteria.value = criteria
    }

    fun resetFilters() {
        _filterCriteria.value = FilterCriteria()
    }

    fun setHabitDone(habitId: String) {
        _operationStatus.value = OperationStatus.InProgress

        viewModelScope.launch {
            try {
                val currentHabit = _habits.value[habitId]
                if (currentHabit != null) {
                    val updatedHabit = currentHabit.copy(done = !currentHabit.done)

                    val result = updateHabitUseCase(habitId, updatedHabit)

                    _operationStatus.value = if (result.isSuccess) {
                        OperationStatus.Success
                    } else {
                        OperationStatus.Error("Failed to update habit status")
                    }
                } else {
                    _operationStatus.value = OperationStatus.Error("Habit not found")
                }

                kotlinx.coroutines.delay(1500)
                resetOperationStatus()

            } catch (e: Exception) {
                Log.e("HabitListViewModel", "Error updating habit status", e)
                _operationStatus.value = OperationStatus.Error("Error: ${e.message}")
            }
        }
    }

    fun resetOperationStatus() {
        _operationStatus.value = null
    }

    // Основной метод фильтрации
    private fun applyFilters(habits: List<Habit>, criteria: FilterCriteria): List<Habit> {
        var filteredHabits = habits

        if (criteria.searchQuery.isNotBlank()) {
            filteredHabits = filteredHabits.filter {
                it.title.contains(criteria.searchQuery, ignoreCase = true) ||
                        it.description.contains(criteria.searchQuery, ignoreCase = true)
            }
        }

        if (criteria.priorityFilters.isNotEmpty()) {
            filteredHabits = filteredHabits.filter { it.priority in criteria.priorityFilters }
        }

        filteredHabits = when (criteria.sortOption) {
            SortOption.CREATION_DATE -> filteredHabits.sortedBy { it.createdAt }
            SortOption.PRIORITY -> filteredHabits.sortedBy { it.priority.ordinal }
            SortOption.ALPHABETICALLY -> filteredHabits.sortedBy { it.title }
        }

        // изменение порядка сортировки (ascending = false)
        if (!criteria.ascending) {
            filteredHabits = filteredHabits.reversed()
        }

        return filteredHabits
    }

    sealed class OperationStatus {
        object InProgress : OperationStatus()
        object Success : OperationStatus()
        data class Error(val message: String) : OperationStatus()
    }
}