package com.example.dailybloom.view

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailybloom.databinding.FragmentHabitTrackerBinding
import com.example.dailybloom.model.Habit
import com.example.dailybloom.viewmodel.HabitListViewModel

class GoodHabitsFragment : Fragment() {

    private lateinit var viewModel: HabitListViewModel
    private var _binding: FragmentHabitTrackerBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: HabitAdapter
    private var fragmentListener: HabitTrackerFragment.HabitFragmentListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment is HabitTrackerFragment.HabitFragmentListener) {
            fragmentListener = parentFragment as HabitTrackerFragment.HabitFragmentListener
        } else {
            throw RuntimeException("Parent fragment must implement HabitFragmentListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHabitTrackerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[HabitListViewModel::class.java]

        setupRecyclerView()
        setupFAB()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = HabitAdapter { openEditScreen(it) }
        binding.habitRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@GoodHabitsFragment.adapter
        }
    }

    private fun updateHabitList(habits: Map<String, Habit>) {
        val goodHabits = habits.values.filter { it.type == "Good" }
        adapter.submitList(goodHabits)
    }

    private fun openEditScreen(habit: Habit) {
        fragmentListener?.onEditHabit(habit)
    }

    private fun setupFAB() {
        binding.fab.setOnClickListener {
            fragmentListener?.onCreateNewHabit()
        }
    }

    private fun observeViewModel() {
        viewModel.habits.observe(viewLifecycleOwner) { habits ->
            updateHabitList(habits)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = GoodHabitsFragment()
    }
}