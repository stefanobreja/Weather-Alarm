package com.obi.weatheralarm.presentation.set_alarm

import android.app.Activity
import android.app.AlertDialog
import android.database.Cursor
import android.media.RingtoneManager
import com.obi.weatheralarm.R
import com.obi.weatheralarm.extensions.getAlarmSounds
import com.obi.weatheralarm.presentation.main.model.AlarmSound
import java.util.*


class SelectAlarmDialog(
    val activity: Activity,
    val currentUri: String,
    val type: Int,
    val onAlarmPicked: (alarmSound: AlarmSound?) -> Unit
) {
    private val builder = AlertDialog.Builder(activity, R.style.AlertDialogTheme)
    private var systemAlarmSounds = ArrayList<AlarmSound>()
    private val alarmList = mutableListOf<String>()
    var selectedItem = AlarmSound(0, "", "")

    init {
        activity.getAlarmSounds(type) {
            systemAlarmSounds = it
            var selectedItem = it
            gotSystemAlarms()
        }

        builder
            .setTitle("Select an alarm")
            .setPositiveButton(
                R.string.confirm
            ) { _, _ ->
                onAlarmPicked(selectedItem)
            }
            .setNegativeButton(
                R.string.cancel
            ) { _, _ ->
            }
        val currentId = 0
        alarmList.forEachIndexed { index, s ->
            if (s == currentUri) {
                currentId == index
            }
        }

        builder.setSingleChoiceItems(alarmList.toTypedArray(), currentId) { _, which ->
            val manager = RingtoneManager(activity)
            manager.setType(RingtoneManager.TYPE_ALARM)
            val cursor: Cursor = manager.cursor

            val alarms: MutableMap<String, String> = HashMap()
            while (cursor.moveToNext()) {
                val title = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX)
                val uri = cursor.getString(RingtoneManager.URI_COLUMN_INDEX)
                val id = cursor.getString(RingtoneManager.ID_COLUMN_INDEX)
                alarms[title] = title
                alarms["$title.id.$id"] = id
                alarms["$title.uri.$uri"] = uri
            }
            val (_id, _uri) = getIdAndUriForAlarmTitle(alarms, which)
            if (!_id.isNullOrEmpty() && !_uri.isNullOrEmpty()) {
                selectedItem =
                    AlarmSound(_id.toInt(), alarms.getValue(alarmList[which]).toString(), _uri)
            }
        }
        builder.create().show()
    }

    private fun gotSystemAlarms() {
        systemAlarmSounds.forEach {
            alarmList.add(it.title)
        }
    }

    private fun getIdAndUriForAlarmTitle(
        alarms: Map<String, String>,
        position: Int
    ): Pair<String?, String?> {
        var id = ""
        var uri = ""
        alarms.forEach {
            if (it.key.split('.').count() == 3) {
                if (it.key.split('.')[0] == alarmList[position] && it.key.split('.')[1] == "id") {
                    id = it.value
                }
                if (it.key.split('.')[0] == alarmList[position] && it.key.split('.')[1] == "uri") {
                    uri = it.value
                }
            }

        }
        return Pair(id, uri)
    }
}
