package com.obi.weatheralarm.domain

import com.obi.weatheralarm.data.AlarmEntity
import com.obi.weatheralarm.presentation.main.model.Alarm

object DomainToDataAlarmEntityMapper {
    fun convert(domainItem: Alarm) =
        AlarmEntity(
            id = domainItem.id,
            name = domainItem.name,
            soundTitle = domainItem.soundTitle,
            soundUri = domainItem.soundUri,
            timeInMilliseconds = domainItem.timeInMilliseconds,
            isActive = true,
            isWeatherActive = domainItem.isActive,
            days = domainItem.days.toMutableList()
        )
}
