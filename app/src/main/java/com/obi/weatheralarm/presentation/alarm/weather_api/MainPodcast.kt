package com.obi.weatheralarm.presentation.main.model.weather_api

import com.google.gson.annotations.SerializedName

data class MainPodcast(
    var temp: Double,
    @SerializedName("feels_like")
    val tempFeelsLike: Double,
    @SerializedName("temp_min")
    var tempMin: Double,
    @SerializedName("temp_max")
    var tempMax: Double,
    val pressure: Double,
    val humidity: Double
)