package com.example.dailybloom.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailybloom.databinding.FragmentHabitsListBinding
import com.example.dailybloom.util.Constants
import com.example.dailybloom.view.adapter.HabitAdapter
import com.example.dailybloom.viewmodel.HabitListViewModel
import com.example.domain.model.Habit
import com.example.domain.model.HabitType
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HabitListFragment : Fragment() {

    private val viewModel: HabitListViewModel by activityViewModels()

    private var _binding: FragmentHabitsListBinding? = null
    private val binding get() = _binding!!

    private var habitType: HabitType = HabitType.GOOD

    private lateinit var adapter: HabitAdapter
    private var fragmentListener: HabitViewPagerFragment.HabitFragmentListener? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentListener = context as? HabitViewPagerFragment.HabitFragmentListener
            ?: throw RuntimeException("$context must implement HabitFragmentListener")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHabitsListBinding.inflate(inflater, container, false)
        arguments?.getString(Constants.ARG_HABIT_TYPE)?.let {
            habitType = HabitType.fromString(it)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupFilterButton()
        collectFlows()
    }

    private fun setupRecyclerView() {
        adapter = HabitAdapter(
            onClick = { habit -> openEditScreen(habit) },
            viewModel = viewModel
        )
        binding.habitRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HabitListFragment.adapter
        }
    }

    private fun setupFilterButton() {
        binding.fabFilter.setOnClickListener {
            showFilterBottomSheet()
        }
    }

    private fun showFilterBottomSheet() {
        val filterBottomSheet = HabitFilterFragment.newInstance()
        filterBottomSheet.show(parentFragmentManager, HabitFilterFragment.TAG)
    }

    private fun openEditScreen(habit: Habit) {
        fragmentListener?.onEditHabit(habit)
    }

    // New method to collect Flow instead of observing LiveData
    private fun collectFlows() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.filteredHabits.collectLatest { filteredHabits ->
                    val habitsOfCurrentType = filteredHabits.filter { it.type == habitType }
                    adapter.submitList(habitsOfCurrentType)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(habitType: String): HabitListFragment {
            return HabitListFragment().apply {
                arguments = Bundle().apply {
                    putString(Constants.ARG_HABIT_TYPE, habitType)
                }
            }
        }
    }
}