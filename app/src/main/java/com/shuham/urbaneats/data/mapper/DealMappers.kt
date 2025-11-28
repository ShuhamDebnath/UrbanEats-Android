package com.shuham.urbaneats.data.mapper

import com.shuham.urbaneats.data.local.entity.DealEntity
import com.shuham.urbaneats.data.remote.dto.DealDto
import com.shuham.urbaneats.domain.model.Deal

fun DealDto.toEntity(): DealEntity {
    return DealEntity(id, title, description, imageUrl, code)
}

fun DealEntity.toDomain(): Deal {
    return Deal(id, title, description, imageUrl, code)
}