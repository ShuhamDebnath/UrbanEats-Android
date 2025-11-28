package com.shuham.urbaneats.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DealDto(
    @SerialName("_id") val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val code: String
)