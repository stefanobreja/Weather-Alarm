package com.obi.weatheralarm.domain

import android.app.Activity
import androidx.room.Room
import com.obi.weatheralarm.data.AlarmEntity
import com.obi.weatheralarm.data.AppDB
import com.obi.weatheralarm.presentation.main.model.Alarm

class AddNewAlarmUseCase() {
    private val getAllAlarmsUseCase = GetAllAlarmsUseCase()
    private var lastAlarmId = 0
    fun invoke(activity: Activity, alarm: Alarm): Response {
        return try {
            val db = Room.databaseBuilder(
                activity, AppDB::
                class.java, "Alarms.db"
            ).fallbackToDestructiveMigration()
                .build()
            val list = getAllAlarmsUseCase.invoke(activity).alarm
            if (list.isNullOrEmpty().not()) {
                lastAlarmId = list?.maxBy { it.id }?.id!!
            }

            val alarmEntity =
                DomainToDataAlarmEntityMapper.convert(alarm.apply { id = lastAlarmId + 1 });
            db.alarmDao().insert(alarmEntity)

            Response(true, alarmEntity)
        } catch (exception: Exception) {
            Response(false, null)
        }
    }

    data class Response(val success: Boolean, val response: AlarmEntity?)
}