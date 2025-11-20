package com.shuham.urbaneats.presentation.navigation

import kotlinx.serialization.Serializable

// 1. Use Objects for screens without arguments
@Serializable
object LoginRoute

@Serializable
object SignUpRoute

@Serializable
object HomeRoute

// 2. Use Data Classes for screens WITH arguments
@Serializable
data class DetailsRoute(
    val foodId: Int,
    val name: String // You can pass multiple args easily now!
)