package com.example.dailybloom.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dailybloom.databinding.ActivityHabitTrackerBinding
import com.example.dailybloom.model.Habit
import com.example.dailybloom.model.HabitChangeListener
import com.example.dailybloom.model.HabitRepositorySingleton
import com.example.dailybloom.viewmodel.HabitViewModel
import com.example.dailybloom.viewmodel.HabitViewModelFactory


class ActivityHabitTracker : AppCompatActivity(), HabitChangeListener {

    private val repository = HabitRepositorySingleton.repository // обращается к синглтону и получаем экземпляр репозитория
    private lateinit var viewModel: HabitViewModel               // переменная, созданая через фабрику
    private lateinit var adapter: HabitAdapter                   // адаптер для RecyclerView, который отвечает за отображение списка привычек
    private lateinit var binding: ActivityHabitTrackerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val factory = HabitViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[HabitViewModel::class.java]

        binding = ActivityHabitTrackerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupFAB()

        updateHabitList(repository.getHabits())
        repository.addListener(this)
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

    override fun onResume() {
        super.onResume()
        updateHabitList(repository.getHabits())
    }

    override fun onDestroy() {
        super.onDestroy()
        repository.removeListener(this)
    }

    override fun onHabitsChanged(habits: Map<String, Habit>) {
        println("This")
    }
}
