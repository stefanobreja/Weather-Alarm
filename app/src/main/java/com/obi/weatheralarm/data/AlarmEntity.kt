package com.obi.weatheralarm.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "alarm_table")
data class AlarmEntity(
    @PrimaryKey
    val id: Int = 0,
    var name: String = "",
    var soundTitle: String = "",
    var soundUri: String = "",
    var timeInMilliseconds: Long = 0,
    val isActive: Boolean = true,
    var isWeatherActive: Boolean = false,
    @TypeConverters(DaysConverters::class)
    val days: List<Int> = emptyList()
)