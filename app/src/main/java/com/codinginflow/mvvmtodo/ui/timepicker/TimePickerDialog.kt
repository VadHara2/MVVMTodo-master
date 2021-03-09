package com.codinginflow.mvvmtodo.ui.timepicker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.fragment.app.viewModels
import com.codinginflow.mvvmtodo.R
import com.codinginflow.mvvmtodo.databinding.TimePickerBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat

@AndroidEntryPoint
class TimePickerDialog: BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = TimePickerBinding.inflate(inflater)
        val viewModel: TimePickerViewModel by viewModels()
        viewModel.beforeText = "${getString(R.string.remind)} "
        binding.apply {

            viewmodel = viewModel
            lifecycleOwner = this@TimePickerDialog

            datePicker.apply {
                maxValue = 300
                minValue = 0
                wrapSelectorWheel = false
                displayedValues = viewModel.formattedDatesList
                descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
                setOnValueChangedListener{ picker, oldVal, newVal ->
                    viewModel.onDateChanged(viewModel.datesList[oldVal], viewModel.datesList[newVal])
                }
                viewModel.onDateChanged(0, viewModel.datesList[0])
            }

            hourPicker.apply {
                maxValue = 23
                minValue = 0
                displayedValues = viewModel.hoursList
                descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
                setOnValueChangedListener{ picker, oldVal, newVal ->
                    viewModel.onHourChanged(oldVal, newVal)
                }
                viewModel.onHourChanged(0,0)
            }

            minutePicker.apply {
                maxValue = 59
                minValue = 0
                displayedValues = viewModel.minutesList
                descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
                setOnValueChangedListener{ picker, oldVal, newVal ->
                    viewModel.onMinuteChanged(oldVal,newVal)
                }
                viewModel.onMinuteChanged(0, 0)
            }
        }

        return binding.root
    }
}