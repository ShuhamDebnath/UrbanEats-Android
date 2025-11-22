package com.shuham.urbaneats.presentation.main

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
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.shuham.urbaneats.presentation.details.DetailRoute
import com.shuham.urbaneats.presentation.favorites.FavoritesRoute
import com.shuham.urbaneats.presentation.favorites.FavoritesState
import com.shuham.urbaneats.presentation.home.HomeRoute
import com.shuham.urbaneats.presentation.navigation.CartRoute
import com.shuham.urbaneats.presentation.navigation.CheckoutRoute
import com.shuham.urbaneats.presentation.navigation.DetailsRoute
import com.shuham.urbaneats.presentation.navigation.FavoritesRoute
import com.shuham.urbaneats.presentation.navigation.HomeRoute
import com.shuham.urbaneats.presentation.navigation.OrdersRoute
import com.shuham.urbaneats.presentation.navigation.ProfileRoute
import com.shuham.urbaneats.presentation.navigation.SearchRoute
import com.shuham.urbaneats.presentation.orders.OrdersRoute
import com.shuham.urbaneats.presentation.profile.ProfileRoute
import com.shuham.urbaneats.presentation.search.SearchRoute
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
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    topLevelRoutes.forEach { screen ->
                        val isSelected = currentDestination?.hierarchy?.any { it.hasRoute(screen.route::class) } == true

                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.label) },
                            label = { Text(screen.label) },
                            selected = isSelected,
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            onClick = {
                                navController.navigate(screen.route) {
                                    // Pop up to the start destination of the graph to
                                    // avoid building up a large stack of destinations
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    // Avoid multiple copies of the same destination
                                    launchSingleTop = true
                                    // Restore state when reselecting a previously selected item
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
                    onFoodClick = { product -> navController.navigate(DetailsRoute(product.id, product.name)) },
                    onCartClick = { navController.navigate(CartRoute) },
                    onProfileClick = { navController.navigate(ProfileRoute) },
                    onSearchClick = {
                        navController.navigate(SearchRoute) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
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
                    onBackClick = { navController.popBackStack() }
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
                        navController.navigate(HomeRoute) {
                            popUpTo(HomeRoute) { inclusive = true }
                        }
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}