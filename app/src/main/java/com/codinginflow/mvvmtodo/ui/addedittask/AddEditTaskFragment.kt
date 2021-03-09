package com.codinginflow.mvvmtodo.ui.addedittask

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.codinginflow.mvvmtodo.R
import com.codinginflow.mvvmtodo.databinding.FragmentAddEditTaskBinding
import com.codinginflow.mvvmtodo.util.exhaustive
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.text.SimpleDateFormat

@AndroidEntryPoint
class AddEditTaskFragment : Fragment(R.layout.fragment_add_edit_task) {

    private val viewModel: AddEditTaskViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentAddEditTaskBinding.bind(view)

        viewModel.liveReminder.postValue("${getString(R.string.reminder_time)} ${SimpleDateFormat("d MMMM',' HH:mm").format(viewModel.taskReminder)}")
        binding.apply {
            viewmodel = viewModel
            lifecycleOwner = this@AddEditTaskFragment

            editTextTaskName.setText(viewModel.taskName)
            checkBoxImportant.isChecked = viewModel.taskImportance
            checkBoxImportant.jumpDrawablesToCurrentState()
            textViewDateCreated.isVisible = viewModel.task != null
            textViewDateCreated.text = "${getString(R.string.date_created)} ${viewModel.task?.createdDateFormatted}"
            editTextTaskName.addTextChangedListener {
                viewModel.taskName = it.toString()
            }

            checkBoxImportant.setOnCheckedChangeListener { _, isChecked ->
                viewModel.taskImportance = isChecked
                if (isChecked) {
                    findNavController().navigate(R.id.action_global_timePickerDialog)
                } else {
                    viewModel.liveImportance.postValue(false)
                }
            }

            fabSaveTask.setOnClickListener {
                viewModel.onSaveClick()
            }
        }

        setFragmentResultListener("set_reminder_request") { _, bundle ->
            val result = bundle.getLong("reminder_result")
            if (result == Long.MIN_VALUE) {
                binding.checkBoxImportant.isChecked = false
                viewModel.taskImportance = false
                viewModel.liveImportance.postValue(false)
            } else {
                viewModel.taskReminder = result
                viewModel.liveReminder.value = "${getString(R.string.reminder_time)} " + SimpleDateFormat("d MMMM',' HH:mm").format(result)
                viewModel.liveImportance.postValue(true)
            }

        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.addEditTaskEvent.collect{event ->
                when (event) {
                    is AddEditTaskViewModel.AddEditTaskEvent.ShowInvalidInputMessage -> {
                        Snackbar.make(requireView(),event.msg, Snackbar.LENGTH_LONG).show()
                    }
                    is AddEditTaskViewModel.AddEditTaskEvent.NavigateBackWithResult -> {
                        binding.editTextTaskName.clearFocus()
                        setFragmentResult(
                                "add_edit_request",
                                bundleOf("add_edit_result" to event.result)
                        )
                        findNavController().popBackStack()
                    }
                }.exhaustive
            }
        }
    }
}