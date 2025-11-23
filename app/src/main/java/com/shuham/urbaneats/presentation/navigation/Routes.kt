package com.shuham.urbaneats.presentation.navigation

import kotlinx.serialization.Serializable

// 1. Use Objects for screens without arguments
@Serializable
object LoginRoute

@Serializable
object SignUpRoute

@Serializable
object HomeRoute

@Serializable
object CartRoute

@Serializable
object CheckoutRoute

@Serializable
object ProfileRoute

@Serializable
object SplashRoute

@Serializable
object SearchRoute

@Serializable
object MainAppRoute

@Serializable
object OrdersRoute

@Serializable
object FavoritesRoute

//@Serializable
//object OrderSuccessRoute
//
//@Serializable
//object TrackOrderRoute

@Serializable
data class OrderSuccessRoute(val orderId: String)

@Serializable
data class TrackOrderRoute(val orderId: String)

// 2. Use Data Classes for screens WITH arguments
@Serializable
data class DetailsRoute(
    val foodId: String,
    val name: String // You can pass multiple args easily now!
)