package com.example.dailybloom.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dailybloom.model.Habit
import com.example.dailybloom.model.HabitChangeListener
import com.example.dailybloom.model.HabitRepository
import com.example.dailybloom.model.HabitType
import com.example.dailybloom.model.Priority

enum class SortOption {
    CREATION_DATE, PRIORITY, ALPHABETICALLY
}

data class FilterCriteria(
    val searchQuery: String = "",
    val sortOption: SortOption = SortOption.CREATION_DATE,
    val priorityFilters: Set<Priority> = setOf(),
    val ascending: Boolean = true,
    val habitType: HabitType? = null,
)

class HabitListViewModel : ViewModel() {

    private val _habits = MutableLiveData(HabitRepository.getHabits())
    val habits: LiveData<Map<String, Habit>> = _habits

    private val habitsListener = HabitChangeListener { habits ->
        _habits.postValue(habits)
        applyFilters()
    }

    private val _filterCriteria = MutableLiveData(FilterCriteria())
    val filterCriteria: LiveData<FilterCriteria> = _filterCriteria

    private val _filteredHabits = MutableLiveData<List<Habit>>(emptyList())
    val filteredHabits: LiveData<List<Habit>> = _filteredHabits

    init {
        HabitRepository.addListener(habitsListener)
        applyFilters()
    }

    fun updateSearchQuery(query: String) {
        _filterCriteria.value = _filterCriteria.value?.copy(searchQuery = query)
        applyFilters()
    }

    fun updateSortOption(sortOption: SortOption) {
        _filterCriteria.value = _filterCriteria.value?.copy(sortOption = sortOption)
        applyFilters()
    }

    fun toggleSortDirection() {
        _filterCriteria.value = _filterCriteria.value?.copy(ascending = !(_filterCriteria.value?.ascending ?: true))
        applyFilters()
    }

    fun updatePriorityFilters(priorities: Set<Priority>) {
        _filterCriteria.value = _filterCriteria.value?.copy(priorityFilters = priorities)
        applyFilters()
    }

    fun resetFilters() {
        _filterCriteria.value = FilterCriteria()
        applyFilters()
    }

    private fun applyFilters() {
        val criteria = _filterCriteria.value ?: FilterCriteria()
        val allHabits = _habits.value?.values?.toList() ?: emptyList()

        var filtered = allHabits

        if (criteria.searchQuery.isNotBlank()) {
            filtered = filtered.filter {
                it.title.contains(criteria.searchQuery, ignoreCase = true) ||
                        it.description.contains(criteria.searchQuery, ignoreCase = true)
            }
        }

        if (criteria.priorityFilters.isNotEmpty()) {
            filtered = filtered.filter { it.priority in criteria.priorityFilters }
        }

        filtered = when (criteria.sortOption) {
            SortOption.CREATION_DATE -> filtered.sortedBy { it.id }
            SortOption.PRIORITY -> filtered.sortedBy { it.priority.ordinal }
            SortOption.ALPHABETICALLY -> filtered.sortedBy { it.title }
        }

        if (!criteria.ascending) {
            filtered = filtered.reversed()
        }
        _filteredHabits.postValue(filtered)
    }

    override fun onCleared() {
        super.onCleared()
        HabitRepository.removeListener(habitsListener)
    }

}
