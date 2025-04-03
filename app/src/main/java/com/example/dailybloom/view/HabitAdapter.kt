package com.example.dailybloom.view

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dailybloom.R
import com.example.dailybloom.model.Habit
import com.example.dailybloom.model.HabitType
import com.example.dailybloom.model.Priority

class HabitAdapter(
    private val onClick: (Habit) -> Unit
) : ListAdapter<Habit, HabitAdapter.HabitViewHolder>(HabitDiffCallback) {

    // Создание ViewHolder для каждого элемента привычки
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_habit, parent, false)
        return HabitViewHolder(view)
    }

    // Привязка данных к ViewHolder
    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        holder.bind(getItem(position), onClick)
    }

    // Класс ViewHolder для обработки каждого элемента привычки
    class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.tvTitle)
        private val priority: TextView = itemView.findViewById(R.id.tvPriority)
        private val type: TextView = itemView.findViewById(R.id.tvType)
        private val frequency: TextView = itemView.findViewById(R.id.tvFrequency)
        private val description: TextView = itemView.findViewById(R.id.tvDescription)
        private val colorIndicator: View = itemView.findViewById(R.id.colorIndicator)

        // Привязка объекта привычки к представлениям
        @SuppressLint("SetTextI18n")
        fun bind(habit: Habit, onClick: (Habit) -> Unit) {
            title.text = habit.title
            description.text = habit.description
            colorIndicator.setBackgroundColor(habit.color)
            priority.text = Priority.toDisplayString(habit.priority)
            type.text = HabitType.toDisplayString(habit.type)
            frequency.text = "${habit.frequency} per ${habit.periodicity}"
            itemView.setOnClickListener { onClick(habit) }
        }
    }

    companion object HabitDiffCallback : DiffUtil.ItemCallback<Habit>() {
        override fun areItemsTheSame(oldItem: Habit, newItem: Habit): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Habit, newItem: Habit): Boolean {
            return oldItem == newItem
        }
    }
}