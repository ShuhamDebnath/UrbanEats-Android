package com.shuham.urbaneats.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.shuham.urbaneats.data.local.dao.CartDao
import com.shuham.urbaneats.data.local.dao.ProductDao
import com.shuham.urbaneats.data.local.entity.CartItemEntity
import com.shuham.urbaneats.data.local.entity.ProductEntity

@Database(
    entities = [ProductEntity::class, CartItemEntity::class],
    version = 3,
    exportSchema = false // Keep it simple for now
)
abstract class UrbanEatsDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao

}