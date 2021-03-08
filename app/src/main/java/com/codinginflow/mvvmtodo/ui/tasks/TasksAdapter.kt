package com.codinginflow.mvvmtodo.ui.tasks

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Adapter
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.codinginflow.mvvmtodo.R
import com.codinginflow.mvvmtodo.data.SortOrder
import com.codinginflow.mvvmtodo.data.Task
import com.codinginflow.mvvmtodo.databinding.DateBinding
import com.codinginflow.mvvmtodo.databinding.ItemTaskBinding
import com.codinginflow.mvvmtodo.di.ApplicationScope
import com.codinginflow.mvvmtodo.util.exhaustive
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import javax.inject.Inject

private val ITEM_VIEW_TYPE_DATE = 0
private val ITEM_VIEW_TYPE_TASK = 1
private const val TAG = "TasksAdapter"

class TasksAdapter(private val listener: OnItemClickListener) : ListAdapter<AdapterItem, RecyclerView.ViewHolder>(DiffCallback()) {

    fun submitListWithDate(list: List<Task>, sortOrder: SortOrder) {
        val superList = mutableListOf<AdapterItem>()
        var displayedDates = mutableListOf<String>()
        for (task in list){
            if (!displayedDates.contains(DateFormat.getDateInstance().format(task.created)) && sortOrder == SortOrder.BY_DATE){
                displayedDates.add(DateFormat.getDateInstance().format(task.created))
                superList.add(AdapterItem.Date(task))
            }
            superList.add(AdapterItem.TaskItem(task))
        }
        val items =  list.map { AdapterItem.TaskItem(it) }

        submitList(superList)
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is AdapterItem.TaskItem -> ITEM_VIEW_TYPE_TASK
            is AdapterItem.Date -> ITEM_VIEW_TYPE_DATE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_VIEW_TYPE_TASK -> TasksViewHolder(ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            ITEM_VIEW_TYPE_DATE -> DateViewHolder(DateBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> throw ClassCastException("Unknown viewType ${viewType}")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TasksViewHolder -> {
                val currentItem = getItem(position) as AdapterItem.TaskItem
                holder.bind(currentItem.task)
            }
            is DateViewHolder -> {
                val currentItem = getItem(position) as AdapterItem.Date
                holder.bind(currentItem.task)
            }
        }
    }

    class DateViewHolder(private val binding: DateBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(task: Task) {

            binding.text.text = SimpleDateFormat("dd MMMM").format(task.created)
        }
    }

    inner class TasksViewHolder(private val binding: ItemTaskBinding) : RecyclerView.ViewHolder(binding.root) {

        init {

            binding.apply {
                root.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val taskItem = getItem(position) as AdapterItem.TaskItem
                        listener.onItemClick(taskItem.task)
                    }
                }
                checkBoxCompleted.setOnClickListener {
                    val position = adapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val taskItem = getItem(position) as AdapterItem.TaskItem
                        listener.onCheckBoxClick(taskItem.task, checkBoxCompleted.isChecked)
                    }
                }
            }
        }

        fun bind(task: Task) {
            binding.apply {
                checkBoxCompleted.isChecked = task.completed
                textViewName.text = task.name
                textViewName.paint.isStrikeThruText = task.completed
                labelPriority.isVisible = task.important
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<AdapterItem>() {
        override fun areItemsTheSame(oldItem: AdapterItem, newItem: AdapterItem) =
                oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: AdapterItem, newItem: AdapterItem) =
                oldItem == newItem
    }

    interface OnItemClickListener {
        fun onItemClick(task: Task)
        fun onCheckBoxClick(task: Task, isChecked: Boolean)
    }

}

sealed class AdapterItem {


    data class TaskItem(val task: Task) : AdapterItem() {
        override val id = task.id
    }

    data class Date(val task: Task) : AdapterItem() {
        override val id = Long.MIN_VALUE
    }

    abstract val id: Long
}