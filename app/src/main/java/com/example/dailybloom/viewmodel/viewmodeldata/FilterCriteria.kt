package com.example.dailybloom.viewmodel.viewmodeldata

import com.example.dailybloom.model.Priority

data class FilterCriteria(
    val searchQuery: String = "",
    val sortOption: SortOption = SortOption.CREATION_DATE,
    val priorityFilters: Set<Priority> = setOf(),
    val ascending: Boolean = true,
)