package com.shuham.urbaneats

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.shuham.urbaneats.presentation.cart.CartRoute
import com.shuham.urbaneats.presentation.details.DetailRoute
import com.shuham.urbaneats.presentation.details.DetailScreen
import com.shuham.urbaneats.presentation.home.HomeRoute
import com.shuham.urbaneats.presentation.home.HomeScreen
import com.shuham.urbaneats.presentation.login.LoginRoute
import com.shuham.urbaneats.presentation.navigation.CartRoute
import com.shuham.urbaneats.presentation.navigation.DetailsRoute
import com.shuham.urbaneats.presentation.navigation.HomeRoute
import com.shuham.urbaneats.presentation.navigation.LoginRoute
import com.shuham.urbaneats.presentation.navigation.SignUpRoute
import com.shuham.urbaneats.presentation.signup.SignUpScreen
import com.shuham.urbaneats.ui.theme.UrbanEatsTheme
import org.koin.compose.KoinContext
import org.koin.core.context.KoinContext


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UrbanEatsTheme {
                KoinContext {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = HomeRoute
                    ) {

                        // 1. Simple Screen
                        composable<LoginRoute> {


                            LoginRoute(
                                onNavigateToHome = {
                                    navController.navigate(HomeRoute) {
                                        popUpTo(LoginRoute) { inclusive = true }
                                    }
                                },
                                onNavigateToSignUp = {
                                    navController.navigate(SignUpRoute)
                                },
                            )

                        }

                        composable<SignUpRoute> {
                            SignUpScreen(onSignUpClick = { name, email, password ->
                                navController.navigate(HomeRoute)

                            }, onLoginClick = {})
                        }

                        composable<HomeRoute> {
                            HomeRoute(
                                onFoodClick = { product ->
                                    //     Type-Safe Argument Passing
                                    navController.navigate(
                                        DetailsRoute(
                                            foodId = product.id,
                                            name = product.name
                                        )
                                    )
                                },
                                onCartClick = {
                                    navController.navigate(CartRoute)
                                }
                            )
                        }


                        composable<CartRoute> {
                            CartRoute(onCheckoutClick = { /* TODO: Day 8 Payment */ })
                        }


                        // 2. Screen with Arguments
                        composable<DetailsRoute> { backStackEntry ->
                            val args = backStackEntry.toRoute<DetailsRoute>()

                            // CRITICAL: We pass the ID to the route
                            DetailRoute(
                                foodId = args.foodId, // Note: Ensure args.foodId is String now, update Routes.kt if it was Int
                                onBackClick = { navController.popBackStack() }
                            )
                        }
                    }
                }


            }
        }
    }
}
