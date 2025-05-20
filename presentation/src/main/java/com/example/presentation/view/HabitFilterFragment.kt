package com.example.presentation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.domain.model.Priority
import com.example.presentation.viewmodel.viewmodeldata.FilterCriteria
import com.example.presentation.viewmodel.HabitListViewModel
import com.example.presentation.viewmodel.viewmodeldata.SortOption
import com.example.presentation.databinding.FragmentHabitFilterBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class HabitFilterFragment : BottomSheetDialogFragment()  {

    private var _binding: FragmentHabitFilterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HabitListViewModel by activityViewModels()

    private var localCriteria: FilterCriteria = FilterCriteria()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHabitFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        collectFlows()
        setupListeners()
    }

    private fun collectFlows() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.filterCriteria.collectLatest { criteria ->
                    localCriteria = criteria.copy()
                    updateUIFromCriteria(localCriteria)
                }
            }
        }
    }

    companion object {
        const val TAG = "HabitFilterBottomSheet"

        fun newInstance(): HabitFilterFragment {
            return HabitFilterFragment()
        }
    }

    private fun setupListeners() {
        with(binding) {

            etSearch.doAfterTextChanged { text ->
                localCriteria = localCriteria.copy(searchQuery = text.toString())
            }

            chipCreationDate.setOnClickListener {
                localCriteria = localCriteria.copy(sortOption = SortOption.CREATION_DATE)
            }

            chipPriority.setOnClickListener {
                localCriteria = localCriteria.copy(sortOption = SortOption.PRIORITY)
            }

            chipAlphabetically.setOnClickListener {
                localCriteria = localCriteria.copy(sortOption = SortOption.ALPHABETICALLY)
            }

            val priorityChips = listOf(chipHigh, chipMedium, chipLow)
            priorityChips.forEach { chip ->
                chip.setOnCheckedChangeListener { _, _ ->
                    updateLocalPriorityFilters()
                }
            }

            btnApplyFilters.setOnClickListener {
                applyFiltersToViewModel()
                dismiss()
            }

            btnResetFilters.setOnClickListener {
                resetUI()
                localCriteria = FilterCriteria()
                viewModel.resetFilters()
            }
        }
    }
    private fun updateLocalPriorityFilters() {
        val selectedPriorities = mutableSetOf<Priority>()

        with(binding) {
            if (chipHigh.isChecked) selectedPriorities.add(Priority.HIGH)
            if (chipMedium.isChecked) selectedPriorities.add(Priority.MEDIUM)
            if (chipLow.isChecked) selectedPriorities.add(Priority.LOW)
        }

        localCriteria = localCriteria.copy(priorityFilters = selectedPriorities)
    }

    private fun applyFiltersToViewModel() {
        viewModel.updateFilters(localCriteria)
    }

    private fun updateUIFromCriteria(criteria: FilterCriteria) {
        with(binding) {
            if (etSearch.text.toString() != criteria.searchQuery) {
                etSearch.setText(criteria.searchQuery)
            }

            when (criteria.sortOption) {
                SortOption.CREATION_DATE -> chipCreationDate.isChecked = true
                SortOption.PRIORITY -> chipPriority.isChecked = true
                SortOption.ALPHABETICALLY -> chipAlphabetically.isChecked = true
            }

            chipHigh.isChecked = Priority.HIGH in criteria.priorityFilters
            chipMedium.isChecked = Priority.MEDIUM in criteria.priorityFilters
            chipLow.isChecked = Priority.LOW in criteria.priorityFilters
        }
    }

    private fun resetUI() {
        with(binding) {
            etSearch.text?.clear()
            chipCreationDate.isChecked = true
            chipPriority.isChecked = false
            chipAlphabetically.isChecked = false
            chipHigh.isChecked = false
            chipMedium.isChecked = false
            chipLow.isChecked = false
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}