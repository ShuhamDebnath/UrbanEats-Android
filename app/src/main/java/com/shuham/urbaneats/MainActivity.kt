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
import com.shuham.urbaneats.presentation.checkout.CheckoutRoute
import com.shuham.urbaneats.presentation.details.DetailRoute
import com.shuham.urbaneats.presentation.home.HomeRoute
import com.shuham.urbaneats.presentation.login.LoginRoute
import com.shuham.urbaneats.presentation.navigation.CartRoute
import com.shuham.urbaneats.presentation.navigation.CheckoutRoute
import com.shuham.urbaneats.presentation.navigation.DetailsRoute
import com.shuham.urbaneats.presentation.navigation.HomeRoute
import com.shuham.urbaneats.presentation.navigation.LoginRoute
import com.shuham.urbaneats.presentation.navigation.ProfileRoute
import com.shuham.urbaneats.presentation.navigation.SignUpRoute
import com.shuham.urbaneats.presentation.navigation.SplashRoute
import com.shuham.urbaneats.presentation.profile.ProfileRoute
import com.shuham.urbaneats.presentation.signup.SignUpScreen
import com.shuham.urbaneats.presentation.splash.SplashRoute
import com.shuham.urbaneats.presentation.splash.SplashScreen
import com.shuham.urbaneats.ui.theme.UrbanEatsTheme
import org.koin.compose.KoinContext


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
                        startDestination = SplashRoute
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
                                },
                                onProfileClick = {
                                    navController.navigate(ProfileRoute)
                                }
                            )
                        }



                        composable<CartRoute> {
                            CartRoute(onCheckoutClick = { navController.navigate(CheckoutRoute) })
                        }

                        composable<CheckoutRoute> {
                            CheckoutRoute(
                                onOrderSuccess = {
                                    // Clear stack and go home (Simple version for now)
                                    navController.navigate(HomeRoute) {
                                        popUpTo(HomeRoute) { inclusive = true }
                                    }
                                    // Ideally, navigate to a specialized "OrderSuccessRoute"
                                },
                                onBackClick = { navController.popBackStack() }
                            )
                        }


                        composable<ProfileRoute> {
                            ProfileRoute(
                                onLogout = {
                                    navController.navigate(LoginRoute) {
                                        popUpTo(0) { inclusive = true } // Clear entire stack
                                    }
                                }
                            )
                        }

                        composable<SplashRoute> {
                            SplashRoute(
                                onNavigateToLogin = {
                                    navController.navigate(LoginRoute)
                                },
                                onNavigateToHome = {
                                    navController.navigate(HomeRoute) {
                                        popUpTo(SplashRoute) { inclusive = true }
                                    }
                                }
                            )
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
