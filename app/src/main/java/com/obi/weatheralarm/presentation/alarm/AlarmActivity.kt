package com.obi.weatheralarm.presentation.alarm

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.media.MediaPlayer
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.speech.tts.TextToSpeech
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.location.*
import com.google.gson.Gson
import com.obi.weatheralarm.R
import com.obi.weatheralarm.domain.GetWeather
import com.obi.weatheralarm.helpers.ALARM_JSON_EXTRA
import com.obi.weatheralarm.helpers.PREFS_KEY
import com.obi.weatheralarm.helpers.round
import com.obi.weatheralarm.presentation.main.model.Alarm
import com.obi.weatheralarm.presentation.main.model.weather_api.WeatherObject
import kotlinx.android.synthetic.main.activity_alarm.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONException
import java.io.IOException
import java.util.*

class AlarmActivity : AppCompatActivity() {
    private lateinit var prefs: SharedPreferences
    private val mp = MediaPlayer()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var textToSpeech: TextToSpeech
    private val getAlarmUseCase = GetWeather()
    private var weather = MutableLiveData<WeatherObject>()
    private var lat = 0.0
    private var long = 0.0
    private lateinit var alarm: Alarm
    lateinit var ringtone: Ringtone

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        textToSpeech = TextToSpeech(this, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR) {
                textToSpeech.language = Locale.CANADA
                textToSpeech
            }
        })
        prefs = applicationContext.getSharedPreferences(PREFS_KEY, Context.MODE_PRIVATE)
        alarm = Gson().fromJson(prefs.getString(ALARM_JSON_EXTRA, ""), Alarm::class.java)
        ringtone = RingtoneManager.getRingtone(this, Uri.parse(alarm.soundUri))

        observeEvents()
        GlobalScope.launch {
            if (alarm.isWeatherActive) {
                getLastLocation()
                getWeather(lat, long)
            } else {
                ringtone =
                    RingtoneManager.getRingtone(this@AlarmActivity, Uri.parse(alarm.soundUri))
                ringtone.isLooping = true
                ringtone.play()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (alarm.isWeatherActive) {
            textToSpeech.shutdown()
            this.finish()
        } else {
            ringtone.stop()
            this.finish()
        }
    }

    private fun observeEvents() {
        weather.observe(this, androidx.lifecycle.Observer {
            if (it != null) {
                GlobalScope.launch {
                    startAlarm(it)
                }
            }
        })
        btnStopAlarm.setOnClickListener {
            if (alarm.isWeatherActive) {
                textToSpeech.shutdown()
                this.finish()
            } else {
                ringtone.stop()
                this.finish()
            }
        }
    }

    private suspend fun startAlarm(weather: WeatherObject) {
        while (true) {
            val toSpeak =
                "Good morning! The weather for today is ${weather.weather[0].main}, the temperature is ${weather.mainPodcast.temp.round(
                    1
                )} degrees celsius, feels like ${weather.mainPodcast.tempFeelsLike} and the wind speed is ${weather.weatherWind.windSpeed} kilometers per hour"
            textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null)
            delay(12000)
        }

    }

    private fun getWeather(lat: Double, long: Double) {
        getAlarmUseCase.invoke(lat, long, object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("MainViewModel", e.localizedMessage!!)
            }

            override fun onResponse(call: Call, response: Response) {
                val gson = Gson()
                val body = response.body?.string()
                GlobalScope.launch(Dispatchers.IO) {
                    try {
                        val weatherObject = gson.fromJson(body, WeatherObject::class.java)
                        weather.postValue(weatherObject)
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }
        })
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                fusedLocationClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        lat = location.latitude
                        long = location.longitude
                    }
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private
    val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation
        }
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // Granted. Start getting the location information
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    companion object {
        private const val PERMISSION_CODE = 42
    }
}