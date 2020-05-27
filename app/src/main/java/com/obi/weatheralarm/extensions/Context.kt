package com.obi.weatheralarm.extensions

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.text.SpannableString
import android.widget.Toast
import com.obi.weatheralarm.R
import com.obi.weatheralarm.helpers.ALARM_SOUND_TYPE_NOTIFICATION
import com.obi.weatheralarm.helpers.Configs
import com.obi.weatheralarm.helpers.SILENT
import com.obi.weatheralarm.presentation.main.model.AlarmSound
import java.util.*

val Context.configs: Configs get() = Configs.newInstance(applicationContext)

fun Context.getFormattedTime(
    timeInMills: Long
): SpannableString {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeInMills
    val hours = calendar.get(Calendar.HOUR_OF_DAY)
    val minutes = calendar.get(Calendar.MINUTE)

    var format = "%01d:%02d"
    val formattedTime = String.format(format, hours, minutes)
    return SpannableString(formattedTime)

}

fun Context.getDefaultAlarmSound(type: Int) =
    AlarmSound(0, getDefaultAlarmTitle(type), getDefaultAlarmUri(type).toString())

fun Context.getDefaultAlarmTitle(type: Int): String {
    val alarmString = getString(R.string.alarm)
    return try {
        RingtoneManager.getRingtone(this, getDefaultAlarmUri(type))?.getTitle(this) ?: alarmString
    } catch (e: Exception) {
        alarmString
    }
}

fun Context.getDefaultAlarmUri(type: Int): Uri =
    RingtoneManager.getDefaultUri(if (type == ALARM_SOUND_TYPE_NOTIFICATION) RingtoneManager.TYPE_NOTIFICATION else RingtoneManager.TYPE_ALARM)

fun Context.getAlarmSounds(
    type: Int,
    callback: (ArrayList<AlarmSound>) -> Unit
) {
    val alarms = ArrayList<AlarmSound>()
    val manager = RingtoneManager(this)
    manager.setType(if (type == ALARM_SOUND_TYPE_NOTIFICATION) RingtoneManager.TYPE_NOTIFICATION else RingtoneManager.TYPE_ALARM)

    try {
        val cursor = manager.cursor
        var curId = 1
        val silentAlarm =
            AlarmSound(curId++, getString(R.string.no_sound), SILENT)
        alarms.add(silentAlarm)

        val defaultAlarm = getDefaultAlarmSound(type)
        alarms.add(defaultAlarm)

        while (cursor.moveToNext()) {
            val title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX)
            var uri = cursor.getString(RingtoneManager.URI_COLUMN_INDEX)
            val id = cursor.getString(RingtoneManager.ID_COLUMN_INDEX)
            if (!uri.endsWith(id)) {
                uri += "/$id"
            }

            val alarmSound = AlarmSound(curId++, title, uri)
            alarms.add(alarmSound)
        }
        callback(alarms)
    } catch (e: Exception) {
        showErrorToast(e.localizedMessage)
        callback(ArrayList())
    }
}

fun Context.showErrorToast(exception: String, length: Int = Toast.LENGTH_LONG) {
    showErrorToast(exception, length)
}