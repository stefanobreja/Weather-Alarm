package com.obi.weatheralarm.presentation.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.obi.weatheralarm.presentation.main.model.Alarm


class MainActivityViewModel : ViewModel() {
    val alarmList = MutableLiveData<List<Alarm>>()

    fun setAdapter() {

    }
}