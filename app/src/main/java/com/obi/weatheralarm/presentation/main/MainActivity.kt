package com.obi.weatheralarm.presentation.main

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.obi.weatheralarm.R
import com.obi.weatheralarm.databinding.ActivityMainBinding
import com.obi.weatheralarm.domain.DeleteAlarmUseCase
import com.obi.weatheralarm.domain.GetAllAlarmsUseCase
import com.obi.weatheralarm.helpers.ALARM_JSON_EXTRA
import com.obi.weatheralarm.helpers.PREFS_KEY
import com.obi.weatheralarm.presentation.alarm.AlarmReceiver
import com.obi.weatheralarm.presentation.main.model.Alarm
import com.obi.weatheralarm.presentation.set_alarm.AlarmDialog
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), AlarmListAdapter.AlarmClickInterface {
    private lateinit var prefs: SharedPreferences
    private val getAllAlarmsUseCase = GetAllAlarmsUseCase()
    private val deleteAlarmUseCase = DeleteAlarmUseCase()
    private lateinit var binding: ActivityMainBinding
    private lateinit var listAdapter: AlarmListAdapter

    private val viewModel by lazy {
        ViewModelProvider(this).get(MainActivityViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        GlobalScope.launch {
            viewModel.alarmList.postValue(getAllAlarmsUseCase.invoke(this@MainActivity).alarm)
        }
        prefs = applicationContext.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)

        checkAndAcceptPermissions()
        observeEvents()

        fab_add.setOnClickListener {
            AlarmDialog(this, null) {
                GlobalScope.launch {
                    setNewAlarm(it)
                    prefs.edit().putString(ALARM_JSON_EXTRA, Gson().toJson(it, Alarm::class.java))
                        .apply()
                    viewModel.alarmList.postValue(getAllAlarmsUseCase.invoke(this@MainActivity).alarm)
                }
            }
        }
    }

    private fun observeEvents() {
        viewModel.alarmList.observe(this, Observer {
            if (it.isNullOrEmpty()) {
                binding.ivAlarm.visibility = View.VISIBLE
                binding.noAlarms.visibility = View.VISIBLE
            } else {
                binding.ivAlarm.visibility = View.GONE
                binding.noAlarms.visibility = View.GONE
            }
            listAdapter = AlarmListAdapter(this, it, this)
            binding.rvAlarms.adapter = listAdapter
            listAdapter.notifyDataSetChanged()
        })
    }

    private fun setNewAlarm(alarm: Alarm) {
        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 111, intent, 0)
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            alarm.timeInMilliseconds,
            pendingIntent
        )
    }

    private fun checkAndAcceptPermissions() {
        if (checkPermissions()) {

        } else {
            requestPermissions()
        }
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.INTERNET
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.INTERNET
            ),

            PERMISSION_CODE
        )
    }

    companion object {
        private const val PERMISSION_CODE = 42
    }

    override fun onSetAlarmCheckClicked(alarm: Alarm) {
        TODO("Not yet implemented")
    }

    override fun onAlarmClicked(alarm: Alarm) {
        AlarmDialog(this, alarm) {
            GlobalScope.launch {
                deleteAlarmUseCase.invoke(activity = this@MainActivity, alarm = alarm)
                viewModel.alarmList.postValue(getAllAlarmsUseCase.invoke(this@MainActivity).alarm)
            }
        }
    }


}