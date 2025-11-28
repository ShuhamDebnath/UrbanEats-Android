package com.shuham.urbaneats.domain.usecase.deal

import com.shuham.urbaneats.domain.model.Deal
import com.shuham.urbaneats.domain.repository.DealRepository
import kotlinx.coroutines.flow.Flow

class GetDailyDealsUseCase(private val repository: DealRepository) {
    fun getDeals(): Flow<List<Deal>> = repository.getDeals()
    suspend fun refreshDeals() = repository.refreshDeals()
}