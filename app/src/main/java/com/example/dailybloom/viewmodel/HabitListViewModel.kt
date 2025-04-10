package com.example.dailybloom.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dailybloom.model.Habit
import com.example.dailybloom.model.HabitChangeListener
import com.example.dailybloom.model.HabitRepository
import com.example.dailybloom.model.Priority
import com.example.dailybloom.viewmodel.viewmodeldata.FilterCriteria
import com.example.dailybloom.viewmodel.viewmodeldata.SortOption


class HabitListViewModel : ViewModel() {

    private val _habits = MutableLiveData(HabitRepository.getHabits())
    val habits: LiveData<Map<String, Habit>> = _habits

    private val habitsListener = HabitChangeListener { habits ->
        _habits.value = habits
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

    // Основной метод фильтрации
    private fun applyFilters() {

        val criteria = _filterCriteria.value ?: FilterCriteria()
        // если значение LiveData не null - получаем текущий объект FilterCriteria с параметрами
        // если null - создаем новый объект со значениями по умолчанию

        val allHabits = _habits.value?.values?.toList() ?: emptyList()
        // получаем полный список привычек

        var filteredHabits = allHabits

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

        // Изменение порядка сортировки (ascending = false)
        if (!criteria.ascending) {
            filteredHabits = filteredHabits.reversed()
        }
        _filteredHabits.postValue(filteredHabits)
    }

    override fun onCleared() {
        super.onCleared()
        HabitRepository.removeListener(habitsListener)
    }
}
