package com.obi.weatheralarm.presentation.set_alarm

import android.app.TimePickerDialog
import android.media.AudioManager
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.obi.weatheralarm.R
import com.obi.weatheralarm.domain.AddNewAlarmUseCase
import com.obi.weatheralarm.extensions.getFormattedTime
import com.obi.weatheralarm.presentation.main.MainActivity
import com.obi.weatheralarm.presentation.main.model.Alarm
import com.obi.weatheralarm.presentation.main.model.AlarmSound
import kotlinx.android.synthetic.main.dialog_alarm.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class AlarmDialog(
    private val activity: MainActivity,
    var alarm: Alarm?,
    val callback: (alarmId: Alarm) -> Unit
) {
    private val view = activity.layoutInflater.inflate(R.layout.dialog_alarm, null)

    private val addNewAlarmUseCase = AddNewAlarmUseCase()


    init {
        if (alarm == null) {
            manageNewAlarm()

        } else {
            manageEditAlarm(alarm!!)
        }
    }

    private fun manageNewAlarm() {
        alarm = Alarm(0, "", "", "", 0, true, false, mutableListOf())
        view.apply {
            tvAlarm.setOnClickListener {
                TimePickerDialog(
                    context,
                    timeSetListener,
                    Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                    Calendar.getInstance().get(Calendar.MINUTE),
                    true
                ).show()
            }
            switchWeather.setOnCheckedChangeListener { _, isChecked ->
                alarm!!.isWeatherActive = isChecked
                if (isChecked) {
                    selectAlarm.visibility = View.GONE
                } else {
                    selectAlarm.visibility = View.VISIBLE
                }
            }

            manageChipGroup(chipGroup)

            selectAlarm.setOnClickListener {
                SelectAlarmDialog(
                    activity = activity,
                    currentUri = alarm!!.soundUri,
                    type = AudioManager.STREAM_ALARM,
                    onAlarmPicked = {
                        if (it != null) {
                            updateSelectedAlarmSound(it)
                        }
                    })
            }
            tvTitle.doAfterTextChanged {
                alarm!!.name = it.toString()
            }
        }

        AlertDialog.Builder(activity)
            .setView(view)
            .setPositiveButton(
                R.string.ok
            ) { _, _ ->
                GlobalScope.launch {
                    addNewAlarmUseCase.invoke(activity = activity, alarm = alarm!!)
                    callback(alarm!!)
                }
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun manageEditAlarm(alarm: Alarm) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = alarm.timeInMilliseconds
        view.apply {
            tvAlarm.text = activity.getFormattedTime(alarm.timeInMilliseconds)
            setChips(chipGroup, alarm)
            switchWeather.isChecked = alarm.isWeatherActive
            selectAlarm.text = alarm.soundTitle
            tvTitle.setText(alarm.name)

            tvAlarm.setOnClickListener {
                TimePickerDialog(
                    context,
                    timeSetListener,
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                ).show()
            }
            switchWeather.setOnCheckedChangeListener { _, isChecked ->
                alarm.isWeatherActive = isChecked
                if (isChecked) {
                    selectAlarm.visibility = View.GONE
                } else {
                    selectAlarm.visibility = View.VISIBLE
                }
            }

            manageChipGroup(chipGroup)

            selectAlarm.setOnClickListener {
                SelectAlarmDialog(
                    activity = activity,
                    currentUri = alarm.soundUri,
                    type = AudioManager.STREAM_ALARM,
                    onAlarmPicked = {
                        if (it != null) {
                            updateSelectedAlarmSound(it)
                        }
                    })
            }
            tvTitle.doAfterTextChanged {
                alarm.name = it.toString()
            }
        }

        AlertDialog.Builder(activity)
            .setView(view)
            .setPositiveButton(
                R.string.delete
            ) { _, _ ->
                GlobalScope.launch {
                    callback(alarm)
                }
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    private fun manageChipGroup(chipGroup: ChipGroup) {
        for (i in 1 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as Chip
            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    alarm?.days?.add(chip.id)
                } else {
                    alarm?.days?.remove(chip.id)
                }
            }
        }
    }

    private val timeSetListener = TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        alarm?.timeInMilliseconds = calendar.timeInMillis
        updateAlarmTime()
    }

    private fun updateAlarmTime() {
        view.tvAlarm.text = alarm?.timeInMilliseconds?.let {
            activity.getFormattedTime(
                it
            )
        }
    }

    private fun updateSelectedAlarmSound(alarmSound: AlarmSound) {
        alarm!!.soundTitle = alarmSound.title
        alarm!!.soundUri = alarmSound.uri
        view.selectAlarm.text = alarmSound.title
    }

    private fun setChips(
        chipGroup: ChipGroup,
        item: Alarm
    ) {
        for (i in 1 until chipGroup.childCount) {
            val chip = chipGroup.getChildAt(i) as Chip
            if (item.days.contains(chip.id)) {
                chip.isEnabled = false
                chip.isChecked = true
            } else {
                chip.isEnabled = false
            }
        }
    }
}