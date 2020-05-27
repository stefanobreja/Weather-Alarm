package com.obi.weatheralarm.presentation.main.model

data class Alarm(
    var id: Int,
    var name: String,
    var soundTitle: String,
    var soundUri: String,
    var timeInMilliseconds: Long,
    var isActive: Boolean = true,
    var isWeatherActive: Boolean,
    var days: MutableList<Int>
)