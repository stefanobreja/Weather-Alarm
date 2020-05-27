package com.obi.weatheralarm.presentation.main.model.weather_api

import com.google.gson.annotations.SerializedName

data class Weather(
    val id: Int,
    val main: String
)
