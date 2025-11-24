package com.shuham.urbaneats.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class SizeOption(
    val name: String,
    val price: Double
)

@Serializable
data class AddonOption(
    val name: String,
    val price: Double
)