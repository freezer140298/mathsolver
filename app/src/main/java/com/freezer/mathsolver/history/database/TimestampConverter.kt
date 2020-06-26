package com.freezer.mathsolver.history.database

import androidx.room.TypeConverter
import java.text.SimpleDateFormat

class TimestampConverter {
    companion object {
        val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    }

    @TypeConverter
    fun fromTimestamp() {

    }
}