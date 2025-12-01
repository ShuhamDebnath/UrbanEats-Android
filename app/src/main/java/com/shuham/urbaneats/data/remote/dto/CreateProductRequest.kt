package com.shuham.urbaneats.data.remote.dto

import com.shuham.urbaneats.domain.model.AddonOption
import com.shuham.urbaneats.domain.model.SizeOption
import kotlinx.serialization.Serializable

@Serializable
data class CreateProductRequest(
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val category: String,
    val sizes: List<SizeOption> = emptyList(),
    val addons: List<AddonOption> = emptyList()
)