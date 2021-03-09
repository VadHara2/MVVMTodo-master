package com.codinginflow.mvvmtodo.ui.addedittask

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codinginflow.mvvmtodo.data.Task
import com.codinginflow.mvvmtodo.data.TaskDao
import com.codinginflow.mvvmtodo.ui.ADD_TASK_RESULT_OK
import com.codinginflow.mvvmtodo.ui.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat

class AddEditTaskViewModel @ViewModelInject constructor(
        private val taskDao: TaskDao,
        @Assisted private val state: SavedStateHandle
) : ViewModel() {

    val task = state.get<Task>("task")
    var taskName = state.get<String>("taskName") ?: task?.name ?: ""
        set(value) {
            field = value
            state.set("taskName", value)
        }

    var taskImportance = state.get<Boolean>("taskImportance") ?: task?.important ?: false
        set(value) {
            field = value
            state.set("taskImportance", value)
        }
    val liveImportance = MutableLiveData<Boolean>(taskImportance)

    var taskReminder = state.get<Long>("taskReminder") ?: task?.reminder ?: Long.MIN_VALUE
        set(value) {
            field = value
            state.set("taskReminder", value)
        }
    val liveReminder = MutableLiveData<String>()

    private val addEditTaskEventChannel = Channel<AddEditTaskEvent>()
    val addEditTaskEvent =addEditTaskEventChannel.receiveAsFlow()

    fun onSaveClick() {
        if (taskName.isBlank()) {
            showInvalidInputMessage("Name cannot be empty")
            return
        }

        if (task != null) {
            val updatedTask = task.copy(name = taskName, important = taskImportance, reminder = taskReminder)
            updateTask(updatedTask)
        } else {
            val newTask = Task(name = taskName, important = taskImportance, reminder = taskReminder)
            createTask(newTask)
        }
    }

    private fun createTask(newTask: Task) = viewModelScope.launch {
        taskDao.insert(newTask)
        addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackWithResult(ADD_TASK_RESULT_OK))
    }

    private fun updateTask(updatedTask: Task) = viewModelScope.launch {
        taskDao.update(updatedTask)
        addEditTaskEventChannel.send(AddEditTaskEvent.NavigateBackWithResult(EDIT_TASK_RESULT_OK))
    }

    private fun showInvalidInputMessage(text:String)=viewModelScope.launch{
        addEditTaskEventChannel.send(AddEditTaskEvent.ShowInvalidInputMessage(text))
    }

    sealed class AddEditTaskEvent{
        data class ShowInvalidInputMessage( val msg:String):AddEditTaskEvent()
        data class NavigateBackWithResult( val result: Int):AddEditTaskEvent()
    }
}