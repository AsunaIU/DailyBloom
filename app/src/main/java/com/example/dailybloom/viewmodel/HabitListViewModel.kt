package com.example.dailybloom.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.dailybloom.model.Habit
import com.example.dailybloom.data.local.HabitRepository
import com.example.dailybloom.viewmodel.viewmodeldata.FilterCriteria
import com.example.dailybloom.viewmodel.viewmodeldata.SortOption
import kotlinx.coroutines.launch


class HabitListViewModel : ViewModel() {

    private val TAG = HabitListViewModel::class.java.simpleName

    private val repositoryHabits: LiveData<Map<String, Habit>> =
        HabitRepository.habits.asLiveData(viewModelScope.coroutineContext)

    private val _filterCriteria = MutableLiveData(FilterCriteria())
    val filterCriteria: LiveData<FilterCriteria> = _filterCriteria

    private val _operationStatus = MutableLiveData<OperationStatus?>()
    val operationStatus: LiveData<OperationStatus?> = _operationStatus

    private val _filteredHabits = MediatorLiveData<List<Habit>>().apply {
        addSource(repositoryHabits) { habits ->
            Log.d(TAG, "repositoryHabits changed: ${habits.size} items")
            value = applyFilters(habits.values.toList(), _filterCriteria.value ?: FilterCriteria())
            Log.d(TAG, "FilteredHabits updated from repositoryHabits: ${value!!.size} items")
        }

        addSource(_filterCriteria) { criteria ->
            Log.d(TAG, "filterCriteria changed: $criteria")
            value = applyFilters(repositoryHabits.value?.values?.toList() ?: emptyList(), criteria)
        }
    }

    val filteredHabits: LiveData<List<Habit>> = _filteredHabits


    fun toggleSortDirection() {
        _filterCriteria.value =
            _filterCriteria.value?.copy(ascending = !(_filterCriteria.value?.ascending ?: true))
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
                val currentHabit = repositoryHabits.value?.get(habitId)
                if (currentHabit != null) {
                    val updatedHabit = currentHabit.copy(done = !currentHabit.done)

                    val result = HabitRepository.updateHabit(habitId, updatedHabit)

                    _operationStatus.value = if (result) {
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