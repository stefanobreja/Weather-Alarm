package com.obi.weatheralarm.domain

import android.util.Log
import com.obi.weatheralarm.helpers.OPEN_API_KEY
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request

class GetWeather {
    private val client = OkHttpClient()
    fun invoke(long: Double, lat: Double, callBack: Callback) {
        val url = "https://api.openweathermap.org/data/2.5/weather?lat=${lat}&lon=${long}&units=metric&appid=$OPEN_API_KEY"
        val request: Request = Request.Builder()
                .url(url)
                .build()
        try {
            val call = client.newCall(request)
            call.enqueue(callBack)
        } catch (e: Exception) {
            Log.e("GetWeatherApi", e.localizedMessage!!)
        }
    }
}