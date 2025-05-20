package com.example.presentation.view

import HabitEditViewModel
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.presentation.util.Constants
import com.example.presentation.viewmodel.viewmodeldata.UiHabit
import com.example.presentation.R
import com.example.presentation.databinding.FragmentCreateHabitBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CreateHabitFragment : Fragment() {

    private val viewModel: HabitEditViewModel by viewModels()

    private var _binding: FragmentCreateHabitBinding? = null
    private val binding get() = _binding!!

    private var fragmentListener: CreateHabitListener? = null

    private var lastAction: LastAction = LastAction.NONE

    companion object {
        fun newInstance(habitId: String? = null): CreateHabitFragment {
            val fragment = CreateHabitFragment()
            val args = Bundle()
            if (habitId != null) {
                args.putString(Constants.ARG_HABIT_ID, habitId)
            }
            fragment.arguments = args
            return fragment
        }
    }

    private enum class LastAction {
        NONE, SAVE, DELETE
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        fragmentListener = context as CreateHabitListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateHabitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        restoreState(savedInstanceState)
        setupUI()
        setupObservers()

        collectFlows()
    }

    private fun collectFlows() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.habits.collect { habitsMap ->
                    Log.d("CreateHabitFragment", "Habits flow collected: ${habitsMap.size} items")
                }
            }
        }
    }

    // сохраняет состояние UI в Bundle
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.uiState.value?.let {
            outState.putParcelable(Constants.KEY_UI_STATE, it)
        }
    }

    // восстанавливает состояние UI из Bundle
    private fun restoreState(savedInstanceState: Bundle?) {
        savedInstanceState?.getParcelable<UiHabit>(Constants.KEY_UI_STATE)?.let {
            viewModel.setUIState(it)
        }
    }

    // фрагмент наблюдает за LiveData из ViewModel (uiState) и вызывает updateUI при изменениях
    private fun setupObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            updateUI(state)
        }

        viewModel.operationStatus.observe(viewLifecycleOwner) { status ->
            when (status) {
                HabitEditViewModel.OperationStatus.Success -> {
                    Toast.makeText(context, "Операция выполнена успешно", Toast.LENGTH_SHORT).show()
                    viewModel.resetOperationStatus()

                    when (lastAction) {
                        LastAction.SAVE -> fragmentListener?.onHabitSaved()
                        LastAction.DELETE -> fragmentListener?.onHabitDeleted()
                        LastAction.NONE -> Log.w("CreateHabitFragment", "Success status received but no action tracked")
                    }
                    lastAction = LastAction.NONE  // Сброс lastAction
                }
                is HabitEditViewModel.OperationStatus.Error -> {
                    Toast.makeText(context, "Ошибка: ${status.message}", Toast.LENGTH_SHORT).show()
                    viewModel.resetOperationStatus()
                }
                HabitEditViewModel.OperationStatus.InProgress -> {
                    // можно сделать ProgressBar
                }
                null -> {
                    // статус ещё не установлен или сброшен — ничего не делаем
                }
            }
        }
    }

    // обновление UI при изменении состояния UiHabit
    private fun updateUI(state: UiHabit) {
        with(binding) {
            if (!etHabitTitle.hasFocus()) etHabitTitle.setText(state.title) // если поле не в фокусе (т.е. не вводятся данные)
            if (!etHabitDescription.hasFocus()) etHabitDescription.setText(state.description)
            if (!etHabitFrequency.hasFocus()) etHabitFrequency.setText(state.frequency)

            spinnerPriority.setSelection(state.priorityPos)
            rgHabitType.check(state.typeId)
            spinnerFrequencyUnit.setSelection(state.periodicityPos)
            colorPicker.setSelectedColor(state.selectedColor)

            // Показать/скрыть кнопку удаления в зависимости от того, редактируем ли мы существующую привычку
            btnDeleteHabit.visibility =
                if (arguments?.getString(Constants.ARG_HABIT_ID) != null) View.VISIBLE else View.GONE
        }
    }

    // получение данных UI из пользовательского ввода и передача во ViewModel
    private fun setupUI() {
        with(binding) {

            // после каждого изменения полей отправляем новое значение во ViewModel (если поле в фокусе)
            etHabitTitle.doAfterTextChanged {
                if (etHabitTitle.hasFocus()) {
                    viewModel.updateUIState(title = it.toString())
                }
            }
            etHabitDescription.doAfterTextChanged {
                if (etHabitDescription.hasFocus()) {
                    viewModel.updateUIState(description = it.toString())
                }
            }
            etHabitFrequency.doAfterTextChanged {
                if (etHabitFrequency.hasFocus()) {
                    viewModel.updateUIState(frequency = it.toString())
                }
            }

            spinnerPriority.onItemSelected { position ->
                viewModel.updateUIState(priorityPos = position)  // position - индекс выбранного пункта
            }

            spinnerFrequencyUnit.onItemSelected { position ->
                viewModel.updateUIState(periodicityPos = position)
            }
            rgHabitType.setOnCheckedChangeListener { _, checkedId ->
                viewModel.updateUIState(typeId = checkedId)
            }

            colorPicker.setOnColorSelectedListener { color ->
                viewModel.updateColor(color)
            }

            btnDeleteHabit.setOnClickListener { showDeleteConfirmationDialog() }

            // при нажатии на кнопку запускаем сохранение в ViewModel и отмечаем действие как SAVE
            btnSaveHabit.setOnClickListener {
                lastAction = LastAction.SAVE
                viewModel.saveHabit()
            }
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.delete_habit_title))
            .setMessage(getString(R.string.delete_habit_message))
            .setPositiveButton(getString(R.string.delete)) { _, _ ->
                lastAction = LastAction.DELETE
                viewModel.deleteHabit()
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    interface CreateHabitListener {
        fun onHabitSaved()
        fun onHabitDeleted()
    }
    // интерфейс - способ уведомить Activity о том, что фрагмент успешно сохранил привычку
    // контракт: «любая внешняя сущность (обычно Activity), желающая реагировать на событие “привычка сохранена”, должна реализовать этот метод»
}

// extension для Spinner (централизует пустую реализацию onNothingSelected)
fun Spinner.onItemSelected(action: (position: Int) -> Unit) {
    onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>, view: View?, position: Int, id: Long
        ) = action(position)

        override fun onNothingSelected(parent: AdapterView<*>) = Unit
    }
}