package com.shuham.urbaneats.data.local.converters

import androidx.room.TypeConverter
import com.shuham.urbaneats.domain.model.AddonOption
import com.shuham.urbaneats.domain.model.SizeOption
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class ProductTypeConverters {

    @TypeConverter
    fun fromSizesList(value: List<SizeOption>?): String {
        return Json.encodeToString(value ?: emptyList())
    }

    @TypeConverter
    fun toSizesList(value: String): List<SizeOption> {
        return try {
            Json.decodeFromString(value)
        } catch (e: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun fromAddonsList(value: List<AddonOption>?): String {
        return Json.encodeToString(value ?: emptyList())
    }

    @TypeConverter
    fun toAddonsList(value: String): List<AddonOption> {
        return try {
            Json.decodeFromString(value)
        } catch (e: Exception) {
            emptyList()
        }
    }
}