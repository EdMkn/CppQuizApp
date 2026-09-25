package com.cppquiz.app.data

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Type converters for Room database
 * Converts List<String> to/from JSON string for storage
 */
class Converters {
    private val gson = Gson()
    
    @TypeConverter
    fun fromStringList(value: String): List<String> {
        if (value.isEmpty()) {
            return emptyList()
        }
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }
    
    @TypeConverter
    fun toStringList(list: List<String>): String {
        return gson.toJson(list)
    }
}



