package com.example.dailybloom.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailybloom.databinding.FragmentHabitsListBinding
import com.example.dailybloom.model.Habit
import com.example.dailybloom.model.HabitType
import com.example.dailybloom.viewmodel.HabitListViewModel

class HabitListFragment : Fragment() {

    private lateinit var viewModel: HabitListViewModel

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
        arguments?.getString(ARG_HABIT_TYPE)?.let {
            habitType = HabitType.fromString(it)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[HabitListViewModel::class.java]

        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = HabitAdapter { habit -> openEditScreen(habit) }
        binding.habitRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@HabitListFragment.adapter
        }
    }

    private fun openEditScreen(habit: Habit) {
        fragmentListener?.onEditHabit(habit)
    }

    private fun observeViewModel() {
        viewModel.habits.observe(viewLifecycleOwner) { habits ->
            val filteredHabits = habits.values.filter { it.type == habitType }
            adapter.submitList(filteredHabits)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_HABIT_TYPE = "habitType"

        fun newInstance(habitType: String): HabitListFragment {
            return HabitListFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_HABIT_TYPE, habitType)
                }
            }
        }
    }
}


