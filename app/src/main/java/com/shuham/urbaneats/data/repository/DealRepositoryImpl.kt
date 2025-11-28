package com.shuham.urbaneats.data.repository

import com.shuham.urbaneats.data.local.dao.DealDao
import com.shuham.urbaneats.data.mapper.toDomain
import com.shuham.urbaneats.data.mapper.toEntity
import com.shuham.urbaneats.data.remote.dto.DealDto
import com.shuham.urbaneats.domain.model.Deal
import com.shuham.urbaneats.domain.repository.DealRepository
import com.shuham.urbaneats.util.NetworkResult
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DealRepositoryImpl(
    private val client: HttpClient,
    private val dao: DealDao
) : DealRepository {

    override fun getDeals(): Flow<List<Deal>> {
        return dao.getDeals().map { entities -> entities.map { it.toDomain() } }
    }

    override suspend fun refreshDeals(): NetworkResult<Unit> {
        return try {
            val response = client.get("api/deals")
            if (response.status == HttpStatusCode.OK) {
                val dtos = response.body<List<DealDto>>()
                dao.clearDeals()
                dao.insertAll(dtos.map { it.toEntity() })
                NetworkResult.Success(Unit)
            } else {
                NetworkResult.Error("Failed to fetch deals")
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message)
        }
    }
}