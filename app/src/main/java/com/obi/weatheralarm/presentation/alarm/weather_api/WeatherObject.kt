package com.obi.weatheralarm.presentation.main.model.weather_api

import com.google.gson.annotations.SerializedName

data class WeatherObject(
    @SerializedName("weather")
    val weather: List<Weather>,
    @SerializedName("main")
    val mainPodcast: MainPodcast,

    @SerializedName("wind")
    val weatherWind: WeatherWind
)