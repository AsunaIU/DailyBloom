package com.example.dailybloom.view

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dailybloom.R
import com.example.dailybloom.model.Habit

class HabitAdapter(
    private val onClick: (Habit) -> Unit
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    private val items = mutableListOf<Habit>()

    fun submitList(newItems: List<Habit>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    // Создание ViewHolder для каждого элемента привычки
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_habit, parent, false)
        return HabitViewHolder(view)
    }

    // Привязка данных к ViewHolder
    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = items[position]
        holder.bind(habit, onClick)
    }

    override fun getItemCount(): Int = items.size

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
            priority.text = habit.priority
            type.text = habit.type
            frequency.text = "${habit.frequency} per ${habit.periodicity}"
            itemView.setOnClickListener { onClick(habit) }
        }
    }
}