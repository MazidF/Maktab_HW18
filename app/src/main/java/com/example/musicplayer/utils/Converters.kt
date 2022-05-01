package com.example.musicplayer.utils

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import java.sql.Date

@ProvidedTypeConverter
class Converters {

    @TypeConverter
    fun Date.toLong() = this.time
}