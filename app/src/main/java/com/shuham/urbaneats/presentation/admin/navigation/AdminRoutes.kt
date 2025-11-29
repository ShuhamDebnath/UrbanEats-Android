package com.shuham.urbaneats.presentation.admin.navigation

import kotlinx.serialization.Serializable

@Serializable
object AdminOrdersRoute

@Serializable
object AdminMenuRoute

@Serializable
data class AddEditProductRoute(val productId: String? = null) // Null = Add, ID = Edit

@Serializable
object AdminProfileRoute