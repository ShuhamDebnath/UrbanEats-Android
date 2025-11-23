package com.shuham.urbaneats.presentation.main

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.shuham.urbaneats.presentation.cart.CartRoute
import com.shuham.urbaneats.presentation.checkout.CheckoutRoute
import com.shuham.urbaneats.presentation.checkout.OrderSuccessRoute
import com.shuham.urbaneats.presentation.details.DetailRoute
import com.shuham.urbaneats.presentation.favorites.FavoritesRoute
import com.shuham.urbaneats.presentation.favorites.FavoritesState
import com.shuham.urbaneats.presentation.home.HomeRoute
import com.shuham.urbaneats.presentation.navigation.CartRoute
import com.shuham.urbaneats.presentation.navigation.CheckoutRoute
import com.shuham.urbaneats.presentation.navigation.DetailsRoute
import com.shuham.urbaneats.presentation.navigation.FavoritesRoute
import com.shuham.urbaneats.presentation.navigation.HomeRoute
import com.shuham.urbaneats.presentation.navigation.OrderSuccessRoute
import com.shuham.urbaneats.presentation.navigation.OrdersRoute
import com.shuham.urbaneats.presentation.navigation.ProfileRoute
import com.shuham.urbaneats.presentation.navigation.SearchRoute
import com.shuham.urbaneats.presentation.navigation.TrackOrderRoute
import com.shuham.urbaneats.presentation.orders.OrdersRoute
import com.shuham.urbaneats.presentation.profile.ProfileRoute
import com.shuham.urbaneats.presentation.search.SearchRoute
import com.shuham.urbaneats.presentation.track_order.TrackOrderRoute
import kotlinx.serialization.Serializable

// Define the Tab Structure

sealed class BottomNavItem(val route: Any, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem(HomeRoute, Icons.Rounded.Home, "Home")
    object Search : BottomNavItem(SearchRoute, Icons.Rounded.Search, "Search")
    object Cart : BottomNavItem(CartRoute, Icons.Rounded.ShoppingCart, "Cart")
    object Profile : BottomNavItem(ProfileRoute, Icons.Rounded.Person, "Profile")
}

@Composable
fun MainScreen(
    onLogout: () -> Unit
) {
    val navController = rememberNavController()

    // Define Top Level Screens (Tabs)
    val topLevelRoutes = listOf(
        BottomNavItem.Home,
        BottomNavItem.Search,
        BottomNavItem.Cart,
        BottomNavItem.Profile
    )

    // Determine if Bottom Bar should be visible
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val showBottomBar = topLevelRoutes.any {
        currentDestination?.hierarchy?.any { dest -> dest.hasRoute(it.route::class) } == true
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                // Clean White Navigation Bar
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    topLevelRoutes.forEach { screen ->
                        val isSelected =
                            currentDestination?.hierarchy?.any { it.hasRoute(screen.route::class) } == true

                        // ANIMATION: Bouncy Scale
                        val scale by animateFloatAsState(
                            targetValue = if (isSelected) 1.1f else 1.0f, // Scale up slightly when selected
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            ),
                            label = "Icon Scale"
                        )

                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = screen.icon,
                                    contentDescription = screen.label,
                                    modifier = Modifier.scale(scale) // Apply Animation
                                )
                            },
                            label = {
                                Text(
                                    text = screen.label,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            selected = isSelected,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color(0xFFE65100), // Brand Orange
                                selectedTextColor = Color(0xFFE65100),
                                indicatorColor = Color(0xFFFFF3E0), // Light Peach Pill
                                unselectedIconColor = Color.Gray,
                                unselectedTextColor = Color.Gray
                            ),
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = HomeRoute,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            // 1. HOME TAB
            composable<HomeRoute> {
                HomeRoute(
                    onFoodClick = { product ->
                        navController.navigate(
                            DetailsRoute(
                                product.id,
                                product.name
                            )
                        )
                    },
                    onCartClick = { navController.navigate(CartRoute) },
                    onProfileClick = { navController.navigate(ProfileRoute) },
                    onSearchClick = {
                        navController.navigate(SearchRoute) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            // 2. SEARCH TAB (Placeholder for now)
            composable<SearchRoute> {
                SearchRoute(
                    onProductClick = { product ->
                        navController.navigate(DetailsRoute(product.id, product.name))
                    }
                )
            }

            // 3. CART TAB
            composable<CartRoute> {
                CartRoute(
                    onCheckoutClick = { navController.navigate(CheckoutRoute) }
                )
            }

            // 4. PROFILE TAB
            composable<ProfileRoute> {
                ProfileRoute(
                    onLogoutSuccess = onLogout,
                    onOrdersClick = { navController.navigate(OrdersRoute) },
                    onFavoritesClick = { navController.navigate(FavoritesRoute) }
                )
            }

            // --- HIDDEN SCREENS (No Bottom Bar) ---

            // ORDERS
            composable<OrdersRoute> {

                OrdersRoute(
                    onBackClick = { navController.popBackStack() },
                    onOrderClick = { order -> navController.navigate(TrackOrderRoute(order.id)) }

                )
            }
            // FAVORITES
            composable<FavoritesRoute> {
                FavoritesRoute(
                    onBackClick = { navController.popBackStack() },
                    onFoodClick = { food ->
                        navController.navigate(DetailsRoute(food.id, food.name))
                    }
                )
            }

            // DETAILS
            composable<DetailsRoute> { backStackEntry ->
                val args = backStackEntry.toRoute<DetailsRoute>()
                DetailRoute(
                    foodId = args.foodId,
                    onBackClick = { navController.popBackStack() }
                )
            }

            // CHECKOUT
            composable<CheckoutRoute> {
                CheckoutRoute(
                    onOrderSuccess = {
                        navController.navigate(OrderSuccessRoute) {
                            popUpTo(OrderSuccessRoute) { inclusive = true }
                        }
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable<OrderSuccessRoute> { backStackEntry ->
                val args = backStackEntry.toRoute<OrderSuccessRoute>()

                OrderSuccessRoute(
                    onHomeClick = {
                        navController.navigate(HomeRoute) {
                            popUpTo(HomeRoute) {
                                inclusive = true
                            }
                        }
                    },
                    onTrackClick = {
                        // Navigate to Tracking with the ID
                        navController.navigate(TrackOrderRoute(args.orderId)) {
                            // Clear Success screen from stack
                            popUpTo(HomeRoute)
                        }
                    }
                )
            }

            composable<TrackOrderRoute> {
                TrackOrderRoute(onBackClick = { navController.popBackStack() })
            }
        }
    }
}