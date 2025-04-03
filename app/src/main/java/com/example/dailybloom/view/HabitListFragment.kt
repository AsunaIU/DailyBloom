package com.example.dailybloom.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailybloom.databinding.FragmentHabitsListBinding
import com.example.dailybloom.model.Habit
import com.example.dailybloom.model.HabitType
import com.example.dailybloom.util.Constants
import com.example.dailybloom.viewmodel.HabitListViewModel

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
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = HabitAdapter { habit -> openEditScreen(habit) }
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

    private fun observeViewModel() {
        viewModel.filteredHabits.observe(viewLifecycleOwner) { filteredHabits ->
            val habitsOfCurrentType = filteredHabits.filter { it.type == habitType }
            adapter.submitList(habitsOfCurrentType)
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


