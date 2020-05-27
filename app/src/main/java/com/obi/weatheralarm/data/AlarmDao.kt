package com.obi.weatheralarm.data

import androidx.room.*

@Dao
interface AlarmDao {
    @Insert
    fun insert(alarm: AlarmEntity)

    @Update
    fun update(alarm: AlarmEntity)

    @Delete
    fun delete(alarm: AlarmEntity)

    @Query("SELECT * FROM alarm_table ORDER BY timeInMilliseconds DESC")
    fun getAllItems(): List<AlarmEntity>

}