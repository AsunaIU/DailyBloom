package com.example.dailybloom.view

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailybloom.databinding.FragmentHabitTrackerBinding
import com.example.dailybloom.model.Habit
import com.example.dailybloom.viewmodel.HabitListViewModel

class BadHabitsFragment : Fragment() {

    private lateinit var viewModel: HabitListViewModel
    private var _binding: FragmentHabitTrackerBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: HabitAdapter
    private var fragmentListener: HabitTrackerFragment.HabitFragmentListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (parentFragment is HabitTrackerFragment.HabitFragmentListener) {
            fragmentListener = parentFragment as HabitTrackerFragment.HabitFragmentListener
        }
        else if (context is HabitTrackerFragment.HabitFragmentListener) {
            fragmentListener = context
        }
        else {
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
            adapter = this@BadHabitsFragment.adapter
        }
    }

    private fun updateHabitList(habits: Map<String, Habit>) {
        val badHabits = habits.values.filter { it.type == "Bad" }
        Log.d("BadHabitsFragment", "Updating bad habits list with ${badHabits.size} items.")
        adapter.submitList(badHabits)
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
        fun newInstance() = BadHabitsFragment()
    }
}