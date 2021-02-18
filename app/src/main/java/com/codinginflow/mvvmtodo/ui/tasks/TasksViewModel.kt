package com.codinginflow.mvvmtodo.ui.tasks

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.codinginflow.mvvmtodo.data.PreferencesManager
import com.codinginflow.mvvmtodo.data.SortOrder
import com.codinginflow.mvvmtodo.data.Task
import com.codinginflow.mvvmtodo.data.TaskDao
import com.codinginflow.mvvmtodo.ui.ADD_TASK_RESULT_OK
import com.codinginflow.mvvmtodo.ui.EDIT_TASK_RESULT_OK
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.prefs.Preferences

class TasksViewModel @ViewModelInject constructor(
        private val taskDao: TaskDao,
        private val preferencesManager: PreferencesManager,
        @Assisted private val state: SavedStateHandle
) : ViewModel() {

    val searchQuery = state.getLiveData("searchQuery", "")

    val preferencesFlow = preferencesManager.preferencesFlow

    private val tasksEventChannel = Channel<TaskEvent>()
    val tasksEvent = tasksEventChannel.receiveAsFlow()

    private val tasksFlow = combine(searchQuery.asFlow(), preferencesFlow) { query, filterPreferences ->
        Pair(query, filterPreferences)
    }.flatMapLatest { (query, filterPreferences) ->
        taskDao.getTasks(query, filterPreferences.sortOrder, filterPreferences.hideCompleted)
    }

    val tasks = tasksFlow.asLiveData()

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        preferencesManager.updateSortOrder(sortOrder)
    }

    fun onHideCompletedClick(hideCompleted: Boolean) = viewModelScope.launch {
        preferencesManager.updateHideCompleted(hideCompleted)
    }

    fun onTaskSelected(task: Task)= viewModelScope.launch {
        tasksEventChannel.send(TaskEvent.NavigateToEditTaskScreen(task))
    }

    fun onTaskCheckedChanged(task: Task, isChecked: Boolean) = viewModelScope.launch {
        taskDao.update(task.copy(completed = isChecked))
    }

    fun onTaskSwiped(task: Task) = viewModelScope.launch {
        taskDao.delete(task)
        tasksEventChannel.send(TaskEvent.ShowUndoDeleteTaskMessage(task))
    }

    fun onUndoDeleteClick(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
    }

    fun onUndoAllDeletedClick(tasks: List<Task>) = viewModelScope.launch {
        for (task in tasks){
            taskDao.insert(task)
        }
    }

    fun onAddNewTaskClick() = viewModelScope.launch {
        tasksEventChannel.send(TaskEvent.NavigateToAddTaskScreen)
    }

    fun onAddEditResult(result: Int){
        when (result) {
            ADD_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task added")
            EDIT_TASK_RESULT_OK -> showTaskSavedConfirmationMessage("Task updated")
        }
    }

    fun onDeleteAllCompletedClick()= viewModelScope.launch {
        tasksEventChannel.send(TaskEvent.NavigateToDeleteAllCompletedScreen)
    }

    fun onDeleteAllCompletedResult(tasks:List<Task>)=viewModelScope.launch{
        tasksEventChannel.send(TaskEvent.ShowUndoDeleteTasksMessage(tasks))
    }

    private fun showTaskSavedConfirmationMessage(text:String) = viewModelScope.launch{
        tasksEventChannel.send(TaskEvent.ShowTaskSavedConfirmationMessage(text))
    }

    sealed class TaskEvent {
        object NavigateToAddTaskScreen: TaskEvent()
        object NavigateToDeleteAllCompletedScreen: TaskEvent()
        data class ShowTaskSavedConfirmationMessage(val msg:String): TaskEvent()
        data class NavigateToEditTaskScreen(val task: Task): TaskEvent()
        data class ShowUndoDeleteTaskMessage(val task: Task) : TaskEvent()
        data class ShowUndoDeleteTasksMessage(val task: List<Task>) : TaskEvent()

    }

}

