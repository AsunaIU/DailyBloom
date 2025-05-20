package com.example.presentation.viewmodel.viewmodeldata

import android.graphics.Color
import android.os.Parcelable
import com.example.domain.model.Periodicity
import com.example.domain.model.Priority
import com.example.presentation.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class UiHabit(
    val title: String = "",
    val description: String = "",
    val priorityPos: Int = Priority.MEDIUM.ordinal,
    val typeId: Int = R.id.rbHabitGood,
    val frequency: String = "1",
    val periodicityPos: Int = Periodicity.DAY.ordinal,
    val selectedColor: Int = Color.WHITE,
    val done: Boolean = false
) : Parcelable
