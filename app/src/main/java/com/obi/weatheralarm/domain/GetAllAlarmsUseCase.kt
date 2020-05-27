package com.obi.weatheralarm.domain

import android.app.Activity
import androidx.room.Room
import com.obi.weatheralarm.data.AppDB
import com.obi.weatheralarm.presentation.main.model.Alarm

class GetAllAlarmsUseCase {

    fun invoke(activity: Activity): Response {
        return try {
            val db = Room.databaseBuilder(
                activity, AppDB::
                class.java, "Alarms.db"
            ).fallbackToDestructiveMigration()
                .build()
            var alarmEntity = db.alarmDao().getAllItems()
            val alarms = mutableListOf<Alarm>()
            alarmEntity.forEach {
                alarms.add(DataToDomainAlarmItemMapper.convert(it))
            }
            Response(alarms)
        } catch (exception: Exception) {
            Response(null)
        }
    }

    data class Response(val alarm: List<Alarm>?)
}