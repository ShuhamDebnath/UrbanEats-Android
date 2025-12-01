package com.shuham.urbaneats.data.remote.dto

import com.shuham.urbaneats.domain.model.AddonOption
import com.shuham.urbaneats.domain.model.SizeOption
import kotlinx.serialization.Serializable

@Serializable
data class UpdateProductRequest(
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String, // Can be URL or Base64
    val category: String,
    val sizes: List<SizeOption>,
    val addons: List<AddonOption>
)