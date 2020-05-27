package com.obi.weatheralarm.presentation.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.obi.weatheralarm.helpers.ALARM_JSON_EXTRA


class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val int = Intent(context, AlarmActivity::class.java)
        int.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context?.startActivity(int)
    }
}