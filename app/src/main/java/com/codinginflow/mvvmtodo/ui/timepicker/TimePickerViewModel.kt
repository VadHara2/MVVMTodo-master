package com.codinginflow.mvvmtodo.ui.timepicker

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.codinginflow.mvvmtodo.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*

private const val TAG = "TimePickerViewModel"

class TimePickerViewModel : ViewModel() {
    lateinit var datesList: Array<Long>
    lateinit var formattedDatesList: Array<String>
    lateinit var hoursList: Array<String>
    lateinit var minutesList: Array<String>

    private val _reminderLong = MutableLiveData<Long>(0)
    val reminderLong: LiveData<Long>
        get() = _reminderLong

    val reminderString = MutableLiveData<String>()

    var beforeText = ""

    init {
        updateLists()
    }

    fun updateLists() {
        datesList = Array(301) { Long.MIN_VALUE }
        formattedDatesList = Array(301) { "" }

        Log.i(TAG, "updateLists: ${Calendar.getInstance().get(Calendar.MINUTE)}")

        for (i in 0..300) {
            datesList.set(
                i, (System.currentTimeMillis() +
                        (i * 86400000).toLong() -
                        (Calendar.getInstance()
                            .get(Calendar.HOUR_OF_DAY) * 3600000 + Calendar.getInstance()
                            .get(Calendar.MINUTE) * 60000 + Calendar.getInstance()
                            .get(Calendar.SECOND) * 1000).toLong())
            )
            formattedDatesList.set(
                i,
                SimpleDateFormat("dd MMMM").format((System.currentTimeMillis() + (i * 86400000).toLong()))
            )
        }

        hoursList = Array(24) { "" }
        for (i in 0..23) {
            if (i < 10) {
                hoursList.set(i, "0" + i.toString())
            } else {
                hoursList.set(i, i.toString())
            }
        }

        minutesList = Array(60) { "" }
        for (i in 0..59) {
            if (i < 10) {
                minutesList.set(i, "0" + i.toString())
            } else {
                minutesList.set(i, i.toString())
            }
        }
    }

    fun onDateChanged(oldDate: Long, newDate: Long) {
        _reminderLong.value = _reminderLong.value?.minus(oldDate)?.plus(newDate)
        reminderString.postValue(beforeText + SimpleDateFormat("d MMMM',' HH:mm").format(_reminderLong.value))
        Log.i(
            TAG,
            "onDateChanged: ${reminderString.value}"
        )
    }

    fun onHourChanged(oldHour: Int, newHour: Int) {
        _reminderLong.value = _reminderLong.value?.minus(oldHour * 3600000)?.plus(newHour * 3600000)
        reminderString.postValue(beforeText + SimpleDateFormat("d MMMM',' HH:mm").format(_reminderLong.value))
    }

    fun onMinuteChanged(oldMinute: Int, newMinute: Int){
        _reminderLong.value = _reminderLong.value?.minus(oldMinute * 60000)?.plus(newMinute * 60000)
        reminderString.postValue(beforeText + SimpleDateFormat("d MMMM',' HH:mm").format(_reminderLong.value))
    }
}