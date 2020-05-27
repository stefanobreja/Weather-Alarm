package com.obi.weatheralarm.helpers

import android.content.Context
import com.google.gson.Gson
import com.obi.weatheralarm.presentation.main.model.Alarm

class Configs(context: Context) {


    val prefs = context.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)

    var alarmLastConfig: Alarm?
        get() = prefs.getString(ALARM_LAST_CONFIG, null)?.let { lastAlarm ->
            gson.fromJson(lastAlarm, Alarm::class.java)
        }
        set(alarm) = prefs.edit().putString(ALARM_LAST_CONFIG, gson.toJson(alarm)).apply()

    var weatherLastConfig: Boolean
        get() = prefs.getBoolean(WEATHER_LAST_CONFIG, false)
        set(isActive) = prefs.edit().putBoolean(WEATHER_LAST_CONFIG, isActive).apply()

    companion object {
        fun newInstance(context: Context) = Configs(context)
        private val gson = Gson()
    }
}