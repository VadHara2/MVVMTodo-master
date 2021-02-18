package com.codinginflow.mvvmtodo.ui.deleteallcompleted

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.codinginflow.mvvmtodo.data.SortOrder
import com.codinginflow.mvvmtodo.data.Task
import com.codinginflow.mvvmtodo.data.TaskDao
import com.codinginflow.mvvmtodo.di.ApplicationScope
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

private const val TAG = "DeleteAllCompletedViewM"
class DeleteAllCompletedViewModel @ViewModelInject constructor(
        private val taskDao: TaskDao,
        @ApplicationScope private val applicationScope: CoroutineScope
):ViewModel() {

    var deletedTasks = listOf<Task>()

    fun OnConfirmClick() = applicationScope.launch {
        val completedTasks = taskDao.getCompletedTask()
        Log.i(TAG, "OnConfirmClick: ${completedTasks.toString()}")
        deletedTasks = completedTasks
        taskDao.deleteCompletedTasks()
    }
}