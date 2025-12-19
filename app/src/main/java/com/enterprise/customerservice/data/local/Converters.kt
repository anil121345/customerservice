package com.enterprise.customerservice.data.local

import androidx.room.TypeConverter
import com.enterprise.customerservice.data.models.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Type converters for Room database
 */
class Converters {
    
    private val gson = Gson()
    
    @TypeConverter
    fun fromQueryStatus(value: QueryStatus): String {
        return value.name
    }
    
    @TypeConverter
    fun toQueryStatus(value: String): QueryStatus {
        return QueryStatus.valueOf(value)
    }
    
    @TypeConverter
    fun fromEscalationReason(value: EscalationReason): String {
        return value.name
    }
    
    @TypeConverter
    fun toEscalationReason(value: String): EscalationReason {
        return EscalationReason.valueOf(value)
    }
    
    @TypeConverter
    fun fromPriority(value: Priority): String {
        return value.name
    }
    
    @TypeConverter
    fun toPriority(value: String): Priority {
        return Priority.valueOf(value)
    }
    
    @TypeConverter
    fun fromEscalationStatus(value: EscalationStatus): String {
        return value.name
    }
    
    @TypeConverter
    fun toEscalationStatus(value: String): EscalationStatus {
        return EscalationStatus.valueOf(value)
    }
    
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return gson.toJson(value)
    }
    
    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType) ?: emptyList()
    }
}
