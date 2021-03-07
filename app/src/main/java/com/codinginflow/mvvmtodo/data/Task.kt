package com.codinginflow.mvvmtodo.data

import android.os.Parcelable
import android.provider.CalendarContract
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat
import java.text.SimpleDateFormat

@Entity(tableName = "task_table")
@Parcelize
data class Task(
    val name: String,
    val important: Boolean = false,
    val completed: Boolean = false,
    val created: Long = System.currentTimeMillis(),
    val reminder: Long = Long.MIN_VALUE,
    @PrimaryKey(autoGenerate = true) val id: Long = 0
) : Parcelable {
    val createdDateFormatted: String
        get() = SimpleDateFormat("dd MMMM yyyy (EEEE)").format(created)
}