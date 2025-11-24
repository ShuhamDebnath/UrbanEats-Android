package com.shuham.urbaneats.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Address(
    @SerialName("_id")
    val id: String = "", // MongoDB ID
    val label: String,
    val fullAddress: String
)