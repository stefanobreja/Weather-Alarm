package com.obi.weatheralarm.domain

import android.app.Activity
import androidx.room.Room
import com.obi.weatheralarm.data.AppDB
import com.obi.weatheralarm.presentation.main.model.Alarm

class UpdateAlarmUseCase {
    fun invoke(activity: Activity, alarm: Alarm): Response {
        return try {
            val db = Room.databaseBuilder(
                activity, AppDB::
                class.java, "Alarms.db"
            ).fallbackToDestructiveMigration()
                .build()

            val alarmEntity =
                DomainToDataAlarmEntityMapper.convert(alarm);
            db.alarmDao().update(alarmEntity)

            Response(true)
        } catch (exception: Exception) {
            Response(false)
        }
    }

    data class Response(val success: Boolean)
}