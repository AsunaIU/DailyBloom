package com.example.dailybloom.view.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.dailybloom.domain.model.Habit

object HabitDiffCallback : DiffUtil.ItemCallback<com.example.dailybloom.domain.model.Habit>() {
    override fun areItemsTheSame(oldItem: com.example.dailybloom.domain.model.Habit, newItem: com.example.dailybloom.domain.model.Habit): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: com.example.dailybloom.domain.model.Habit, newItem: com.example.dailybloom.domain.model.Habit): Boolean {
        return oldItem == newItem
    }
}