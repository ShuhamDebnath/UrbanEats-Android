package com.shuham.urbaneats.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    @SerialName("_id") val id: String,
    val name: String,
    val imageUrl: String
)