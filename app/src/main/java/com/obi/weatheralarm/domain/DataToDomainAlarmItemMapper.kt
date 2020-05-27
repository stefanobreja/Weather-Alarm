package com.obi.weatheralarm.domain

import com.obi.weatheralarm.data.AlarmEntity
import com.obi.weatheralarm.presentation.main.model.Alarm

object DataToDomainAlarmItemMapper {
    fun convert(dataItem: AlarmEntity) =
        Alarm(
            id = dataItem.id,
            name = dataItem.name,
            soundTitle = dataItem.soundTitle,
            soundUri = dataItem.soundUri,
            timeInMilliseconds = dataItem.timeInMilliseconds,
            isActive = true,
            isWeatherActive = dataItem.isActive,
            days = dataItem.days.toMutableList()
        )
}
