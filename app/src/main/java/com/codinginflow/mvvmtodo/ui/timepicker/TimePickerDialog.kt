package com.codinginflow.mvvmtodo.ui.timepicker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import androidx.core.os.bundleOf
import androidx.core.view.get
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.codinginflow.mvvmtodo.R
import com.codinginflow.mvvmtodo.databinding.TimePickerBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class TimePickerDialog : BottomSheetDialogFragment() {

    var result = Long.MIN_VALUE

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = TimePickerBinding.inflate(inflater)
        val viewModel: TimePickerViewModel by viewModels()

        var isCurrentHour = true
        var isCurrentDate = true

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
                setOnValueChangedListener { picker, oldVal, newVal ->
                    isCurrentDate = newVal == 0
                    viewModel.onDateChanged(
                        viewModel.datesList[oldVal],
                        viewModel.datesList[newVal]
                    )
                }
                viewModel.onDateChanged(0, viewModel.datesList[0])
            }

            hourPicker.apply {
                maxValue = 23
                minValue = 0
                value = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                displayedValues = viewModel.hoursList
                descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
                setOnValueChangedListener { picker, oldVal, newVal ->
                    isCurrentHour = newVal == Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                    if (newVal < Calendar.getInstance()
                            .get(Calendar.HOUR_OF_DAY) && isCurrentDate
                    ) {
                        value = oldVal
                    } else {
                        viewModel.onHourChanged(oldVal, newVal)
                    }
                }
                viewModel.onHourChanged(0, Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
            }

            minutePicker.apply {
                maxValue = 59
                minValue = 0
                value = Calendar.getInstance().get(Calendar.MINUTE) + 2
                displayedValues = viewModel.minutesList
                descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
                setOnValueChangedListener { picker, oldVal, newVal ->
                    if (newVal < Calendar.getInstance().get(Calendar.MINUTE) && isCurrentHour) {
                        value = oldVal
                    } else {
                        viewModel.onMinuteChanged(oldVal, newVal)
                    }
                }
                viewModel.onMinuteChanged(0, Calendar.getInstance().get(Calendar.MINUTE) + 2)
            }

            button.setOnClickListener {
                result = viewModel.reminderLong.value!!
                findNavController().popBackStack()
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        setFragmentResult(
            "set_reminder_request",
            bundleOf("reminder_result" to result)
        )
    }
}