package com.shuham.urbaneats.domain.repository

import com.shuham.urbaneats.domain.model.Deal
import com.shuham.urbaneats.util.NetworkResult
import kotlinx.coroutines.flow.Flow

interface DealRepository {
    fun getDeals(): Flow<List<Deal>>
    suspend fun refreshDeals(): NetworkResult<Unit>
}