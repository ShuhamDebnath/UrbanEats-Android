package com.shuham.urbaneats.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordRequest(
    val userId: String,
    val oldPassword: String,
    val newPassword: String
)