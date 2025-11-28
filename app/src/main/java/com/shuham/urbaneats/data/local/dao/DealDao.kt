package com.shuham.urbaneats.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shuham.urbaneats.data.local.entity.DealEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DealDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(deals: List<DealEntity>)

    @Query("SELECT * FROM deals")
    fun getDeals(): Flow<List<DealEntity>>

    @Query("DELETE FROM deals")
    suspend fun clearDeals()
}