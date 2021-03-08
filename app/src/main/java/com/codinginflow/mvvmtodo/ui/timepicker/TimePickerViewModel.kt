package com.codinginflow.mvvmtodo.ui.timepicker

import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat

class TimePickerViewModel : ViewModel() {
    lateinit var datesList: Array<String>
    lateinit var hoursList: Array<String>
    lateinit var minutesList: Array<String>

    init {
        updateLists()
    }

    fun updateLists() {

        datesList = Array(301){""}
        for (i in 0..300) {
            datesList.set(i,SimpleDateFormat("dd MMMM").format((System.currentTimeMillis() + (i * 86400000).toLong())))
        }

        hoursList = Array(24){""}
        for (i in 0..23){
            if (i<10){
                hoursList.set(i,"0" + i.toString())
            }else{
                hoursList.set(i, i.toString())
            }
        }

        minutesList = Array(60){""}
        for (i in 0..59){
            if (i<10){
                minutesList.set(i,"0" + i.toString())
            }else{
                minutesList.set(i, i.toString())
            }
        }
    }
}