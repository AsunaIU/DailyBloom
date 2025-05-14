package com.example.dailybloom.viewmodel

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


class HabitListViewModel : ViewModel() {

    private val repositoryHabits: LiveData<Map<String, Habit>> =
        HabitRepository.habits.asLiveData(viewModelScope.coroutineContext)

    private val _filterCriteria = MutableLiveData(FilterCriteria())
    val filterCriteria: LiveData<FilterCriteria> = _filterCriteria

    private val _filteredHabits = MediatorLiveData<List<Habit>>().apply {
        addSource(repositoryHabits) { habits ->
            value = applyFilters(habits.values.toList(), _filterCriteria.value ?: FilterCriteria())
        }

        addSource(_filterCriteria) { criteria ->
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
            SortOption.PRIORITY -> filteredHabits.sortedBy { it.priority.ordinal } // сортировка по приоритету 2.0
            SortOption.ALPHABETICALLY -> filteredHabits.sortedBy { it.title }
        }

        // изменение порядка сортировки (ascending = false)
        if (!criteria.ascending) {
            filteredHabits = filteredHabits.reversed()
        }

        return filteredHabits
    }
}
