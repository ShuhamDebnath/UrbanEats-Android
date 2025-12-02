package com.shuham.urbaneats.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.shuham.urbaneats.data.local.converters.ProductTypeConverters
import com.shuham.urbaneats.data.local.dao.CartDao
import com.shuham.urbaneats.data.local.dao.DealDao
import com.shuham.urbaneats.data.local.dao.ProductDao
import com.shuham.urbaneats.data.local.entity.CartItemEntity
import com.shuham.urbaneats.data.local.entity.DealEntity
import com.shuham.urbaneats.data.local.entity.ProductEntity

@Database(
    entities = [ProductEntity::class, CartItemEntity::class, DealEntity::class],
    version = 1,
    exportSchema = false // Keep it simple for now
)
@TypeConverters(ProductTypeConverters::class)
abstract class UrbanEatsDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun dealDao(): DealDao

}