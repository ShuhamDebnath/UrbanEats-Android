package com.shuham.urbaneats.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Deal(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String,
    val code: String
)