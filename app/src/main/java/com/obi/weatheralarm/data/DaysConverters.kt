package com.obi.weatheralarm.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


open class DaysConverters {
    @TypeConverter
    fun fromJson(value: String): List<Int>? {
        val gson = Gson()
        val type: Type = object : TypeToken<List<Int?>?>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun dayListToJson(value: List<Int>?): String? {
        val gson = Gson()
        val type: Type = object : TypeToken<List<Int?>?>() {}.type
        return gson.toJson(value, type)
    }
}
