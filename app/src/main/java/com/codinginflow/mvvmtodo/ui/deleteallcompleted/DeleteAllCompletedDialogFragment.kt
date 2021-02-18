package com.codinginflow.mvvmtodo.ui.deleteallcompleted

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.codinginflow.mvvmtodo.data.Task
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteAllCompletedDialogFragment: DialogFragment() {

    private val viewModel: DeleteAllCompletedViewModel by viewModels()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
            AlertDialog.Builder(requireContext())
                    .setTitle("Confirm deletion")
                    .setMessage("Do you really want to delete all completed tasks?")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Yes"){_,_ ->
                        viewModel.OnConfirmClick()
                    }
                    .create()

    override fun onDestroy() {
        super.onDestroy()
        setFragmentResult(
                "delete_all_completed_request",
                bundleOf("delete_all_completed_result" to viewModel.deletedTasks)
        )
    }
}