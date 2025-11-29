package com.shuham.urbaneats.presentation.admin

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.automirrored.outlined.ListAlt
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Fastfood
import androidx.compose.material.icons.outlined.Person
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
import com.shuham.urbaneats.presentation.admin.navigation.*
import com.shuham.urbaneats.presentation.admin.screens.AdminOrdersScreen
import com.shuham.urbaneats.presentation.admin.screens.AdminMenuScreen
import com.shuham.urbaneats.presentation.admin.screens.AddEditProductScreen
import com.shuham.urbaneats.presentation.admin.screens.AdminProfileScreen

sealed class AdminBottomNavItem(
    val route: Any,
    val selectedIcon: ImageVector,
    val unSelectedIcon: ImageVector,
    val label: String
) {
    object Orders : AdminBottomNavItem(
        AdminOrdersRoute,
        Icons.AutoMirrored.Filled.ListAlt,
        Icons.AutoMirrored.Outlined.ListAlt,
        "Orders"
    )

    object Menu :
        AdminBottomNavItem(AdminMenuRoute, Icons.Default.Fastfood, Icons.Outlined.Fastfood, "Menu")

    object Profile :
        AdminBottomNavItem(AdminProfileRoute, Icons.Default.Person, Icons.Outlined.Person, "Admin")
}

@Composable
fun AdminMainScreen(
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val topLevelRoutes = listOf(
        AdminBottomNavItem.Orders,
        AdminBottomNavItem.Menu,
        AdminBottomNavItem.Profile
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Hide bottom bar on Add/Edit Product screen
    val showBottomBar = currentDestination?.hasRoute(AddEditProductRoute::class) == false

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    topLevelRoutes.forEach { screen ->
                        val isSelected =
                            currentDestination?.hierarchy?.any { it.hasRoute(screen.route::class) } == true
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) screen.selectedIcon else screen.unSelectedIcon,
                                    contentDescription = screen.label
                                )
                            },
                            alwaysShowLabel = false,
                            label = { Text(screen.label) },
                            selected = isSelected,
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
            startDestination = AdminOrdersRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            // 1. ORDERS
            composable<AdminOrdersRoute> {
                AdminOrdersScreen()
            }

            // 2. MENU
            composable<AdminMenuRoute> {
                AdminMenuScreen(
                    onAddProductClick = { navController.navigate(AddEditProductRoute(null)) },
                    onEditProductClick = { id -> navController.navigate(AddEditProductRoute(id)) }
                )
            }

            // 3. ADD/EDIT PRODUCT (Hidden Tab)
            composable<AddEditProductRoute> { backStackEntry ->
                // val args = backStackEntry.toRoute<AddEditProductRoute>()
                AddEditProductScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }

            // 4. PROFILE
            composable<AdminProfileRoute> {
                AdminProfileScreen(onLogout = onLogout)
            }
        }
    }
}