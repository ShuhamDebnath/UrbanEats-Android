package com.shuham.urbaneats.presentation.main

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
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
import com.shuham.urbaneats.presentation.address.AddressListRoute
import com.shuham.urbaneats.presentation.cart.CartRoute
import com.shuham.urbaneats.presentation.checkout.CheckoutRoute
import com.shuham.urbaneats.presentation.checkout.OrderFailureRoute
import com.shuham.urbaneats.presentation.checkout.OrderSuccessRoute
import com.shuham.urbaneats.presentation.details.DetailRoute
import com.shuham.urbaneats.presentation.favorites.FavoritesRoute
import com.shuham.urbaneats.presentation.home.HomeRoute
import com.shuham.urbaneats.presentation.navigation.AddressListRoute
import com.shuham.urbaneats.presentation.navigation.CartRoute
import com.shuham.urbaneats.presentation.navigation.CheckoutRoute
import com.shuham.urbaneats.presentation.navigation.DetailsRoute
import com.shuham.urbaneats.presentation.navigation.FavoritesRoute
import com.shuham.urbaneats.presentation.navigation.HelpSupportRoute
import com.shuham.urbaneats.presentation.navigation.HomeRoute
import com.shuham.urbaneats.presentation.navigation.NoInternetRoute
import com.shuham.urbaneats.presentation.navigation.OrderFailureRoute
import com.shuham.urbaneats.presentation.navigation.OrderSuccessRoute
import com.shuham.urbaneats.presentation.navigation.OrdersRoute
import com.shuham.urbaneats.presentation.navigation.ProfileRoute
import com.shuham.urbaneats.presentation.navigation.SearchRoute
import com.shuham.urbaneats.presentation.navigation.SettingsRoute
import com.shuham.urbaneats.presentation.navigation.TrackOrderRoute
import com.shuham.urbaneats.presentation.orders.OrdersRoute
import com.shuham.urbaneats.presentation.profile.HelpSupportRoute
import com.shuham.urbaneats.presentation.profile.ProfileRoute
import com.shuham.urbaneats.presentation.search.SearchRoute
import com.shuham.urbaneats.presentation.settings.SettingsRoute
import com.shuham.urbaneats.presentation.track_order.TrackOrderRoute

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
                    containerColor = Color.White, // Explicit White for clean look
                    tonalElevation = 8.dp,
                    modifier = Modifier.shadow(8.dp) // Add shadow for separation
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
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            selected = isSelected,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary, // Orange
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant, // Gray
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
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
                    onSearchClick = {
                        navController.navigate(SearchRoute) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    //onProfileClick = { navController.navigate(ProfileRoute) }
                    onAddressClick = { navController.navigate(AddressListRoute) }
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
                    onFavoritesClick = { navController.navigate(FavoritesRoute) },
                    onAddressClick = { navController.navigate(AddressListRoute) },
                    onHelpClick = { navController.navigate(HelpSupportRoute) },
                    onSettingsClick = { navController.navigate(SettingsRoute) }
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

            // ADDRESS
            composable<AddressListRoute> {
                AddressListRoute(
                    onBackClick = { navController.popBackStack() },
                    onAddressSelected = { address ->
                        // Pass result back to Checkout
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set("selected_address", address.fullAddress)

                        navController.popBackStack()
                    }
                )
            }
            // Help and Support
            composable<HelpSupportRoute> {
                HelpSupportRoute(
                    onBackClick = { navController.popBackStack() }
                )
            }

            // SETTINGS SCREEN
            composable<SettingsRoute> {
                SettingsRoute(
                    onBackClick = { navController.popBackStack() }
                )
            }

            // DETAILS
            composable<DetailsRoute> { backStackEntry ->
                val args = backStackEntry.toRoute<DetailsRoute>()
                DetailRoute(
                    foodId = args.foodId,
                    onBackClick = { navController.popBackStack() },
                    // NAVIGATE TO CART
                    onGoToCart = {
                        navController.navigate(CartRoute) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            // CHECKOUT
            composable<CheckoutRoute> {
                CheckoutRoute(
                    onOrderSuccess = { orderId ->
                        navController.navigate(OrderSuccessRoute(orderId)) {
                            popUpTo(CheckoutRoute) { inclusive = true }
                        }
                    },
                    onOrderFailure = { reason ->
                        navController.navigate(OrderFailureRoute(reason))
                    },
                    onNoInternet = {
                        // TRIGGER THE GLOBAL NO INTERNET SCREEN
                        navController.navigate(NoInternetRoute)
                    },
                    onEditAddress = { navController.navigate(AddressListRoute) },
                    onBackClick = { navController.popBackStack() },

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

            // FAILURE SCREEN ROUTE
            composable<OrderFailureRoute> { backStackEntry ->
                val args = backStackEntry.toRoute<OrderFailureRoute>()
                OrderFailureRoute(
                    reason = args.reason,
                    onRetryClick = {
                        // Simply pop back to Checkout to try again
                        navController.popBackStack()
                    },
                    onBackToCartClick = {
                        // Go back to Cart, clear Checkout from stack
                        navController.popBackStack(CartRoute, inclusive = false)
                    }
                )
            }

            composable<TrackOrderRoute> {
                TrackOrderRoute(onBackClick = { navController.popBackStack() })
            }

//            composable<NoInternetRoute> {
//                NoInternetScreen(
//                    onRetry = {
//                        // Logic to check internet or pop back
//                        navController.popBackStack()
//                    }
//                )
//            }
        }
    }
}