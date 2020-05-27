package com.obi.weatheralarm.presentation.main.model.weather_api

import com.google.gson.annotations.SerializedName

data class WeatherWind(
    @SerializedName("speed")
    val windSpeed: Double
)