package com.codinginflow.mvvmtodo.ui.timepicker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.fragment.app.viewModels
import com.codinginflow.mvvmtodo.R
import com.codinginflow.mvvmtodo.databinding.TimePickerBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TimePickerDialog: BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = TimePickerBinding.inflate(inflater)
        val viewModel: TimePickerViewModel by viewModels()

        binding.apply {

            datePicker.apply {
                maxValue = 300
                minValue = 0
                wrapSelectorWheel = false
                displayedValues = viewModel.datesList
                descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            }

            hourPicker.apply {
                maxValue = 23
                minValue = 0
                displayedValues = viewModel.hoursList
                descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            }

            minutePicker.apply {
                maxValue = 59
                minValue = 0
                displayedValues = viewModel.minutesList
                descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            }
        }

        return binding.root
    }
}