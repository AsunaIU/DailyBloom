package com.example.dailybloom.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailybloom.databinding.ActivityHabitTrackerBinding
import com.example.dailybloom.model.Habit
import com.example.dailybloom.viewmodel.HabitListViewModel


class ActivityHabitTracker : AppCompatActivity() {

    private lateinit var viewModel: HabitListViewModel
    private lateinit var adapter: HabitAdapter
    private lateinit var binding: ActivityHabitTrackerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[HabitListViewModel::class.java]

        binding = ActivityHabitTrackerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupFAB()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = HabitAdapter { openEditScreen(it) }
        binding.habitRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@ActivityHabitTracker)
            adapter = this@ActivityHabitTracker.adapter
        }
    }

    private fun updateHabitList(habits: Map<String, Habit>) {
        Log.d("ActivityHabitTracker", "Updating habit list with ${habits.size} items.")
        adapter.submitList(habits.values.toList())
    }

    private fun openEditScreen(habit: Habit) {
        startActivity(Intent(this, ActivityCreateHabit::class.java).apply {
            putExtra("HABIT", habit)
        })
    }

    private fun setupFAB() {
        binding.fab.setOnClickListener {
            startActivity(Intent(this, ActivityCreateHabit::class.java))
        }
    }

    private fun observeViewModel() {
        viewModel.habits.observe(this) { habits ->
            Log.d("ActivityHabitTracker", "Updating habit list with ${habits.size} items.")
            updateHabitList(habits)
        }
    }
}