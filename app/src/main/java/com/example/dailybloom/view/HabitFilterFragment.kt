package com.example.dailybloom.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import com.example.dailybloom.databinding.FragmentHabitFilterBinding
import com.example.dailybloom.model.Priority
import com.example.dailybloom.viewmodel.FilterCriteria
import com.example.dailybloom.viewmodel.HabitListViewModel
import com.example.dailybloom.viewmodel.SortOption
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class HabitFilterFragment : BottomSheetDialogFragment()  {

    private var _binding: FragmentHabitFilterBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HabitListViewModel by activityViewModels()

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

        setupListeners()
        observeViewModel()
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
                viewModel.updateSearchQuery(text.toString())
            }

            chipCreationDate.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) viewModel.updateSortOption(SortOption.CREATION_DATE)
            }

            chipPriority.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) viewModel.updateSortOption(SortOption.PRIORITY)
            }

            chipAlphabetically.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) viewModel.updateSortOption(SortOption.ALPHABETICALLY)
            }

            val priorityChips = listOf(chipHigh, chipMedium, chipLow)
            priorityChips.forEach { chip ->
                chip.setOnCheckedChangeListener { _, _ ->
                    updatePriorityFilters()
                }
            }

            btnApplyFilters.setOnClickListener {
                dismiss()
            }

            btnResetFilters.setOnClickListener {
                resetUI()
                viewModel.resetFilters()
            }
        }
    }
    private fun updatePriorityFilters() {
        val selectedPriorities = mutableSetOf<Priority>()

        with(binding) {
            if (chipHigh.isChecked) selectedPriorities.add(Priority.HIGH)
            if (chipMedium.isChecked) selectedPriorities.add(Priority.MEDIUM)
            if (chipLow.isChecked) selectedPriorities.add(Priority.LOW)
        }

        viewModel.updatePriorityFilters(selectedPriorities)
    }

    private fun observeViewModel() {
        viewModel.filterCriteria.observe(viewLifecycleOwner) { criteria ->
            updateUIFromCriteria(criteria)
        }
    }

    private fun updateUIFromCriteria(criteria: FilterCriteria) {
        with(binding) {
            if (etSearch.text.toString() != criteria.searchQuery) {
                etSearch.setText(criteria.searchQuery)
            }

            chipCreationDate.isChecked = criteria.sortOption == SortOption.CREATION_DATE
            chipAlphabetically.isChecked = criteria.sortOption == SortOption.ALPHABETICALLY

            chipHigh.isChecked = Priority.HIGH in criteria.priorityFilters
            chipMedium.isChecked = Priority.MEDIUM in criteria.priorityFilters
            chipLow.isChecked = Priority.LOW in criteria.priorityFilters
        }
    }

    private fun resetUI() {
        with(binding) {
            etSearch.text?.clear()
            chipCreationDate.isChecked = true
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
