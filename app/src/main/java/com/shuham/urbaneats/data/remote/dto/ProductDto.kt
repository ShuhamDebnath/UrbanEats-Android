package com.shuham.urbaneats.data.remote.dto

import com.shuham.urbaneats.domain.model.AddonOption
import com.shuham.urbaneats.domain.model.SizeOption
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class ProductDto(
    @SerialName("_id") val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val rating: Double,
    val category: String,
    // NEW FIELDS (Default to empty list if missing in JSON)
    val sizes: List<SizeOption> = emptyList(),
    val addons: List<AddonOption> = emptyList()
)