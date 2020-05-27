package com.obi.weatheralarm.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [AlarmEntity::class], version = 1)
@TypeConverters(DaysConverters::class)
abstract class AppDB : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao
}