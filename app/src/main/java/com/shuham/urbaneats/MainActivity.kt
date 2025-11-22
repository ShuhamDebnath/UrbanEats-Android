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
import com.shuham.urbaneats.presentation.main.MainScreen
import com.shuham.urbaneats.presentation.navigation.CartRoute
import com.shuham.urbaneats.presentation.navigation.CheckoutRoute
import com.shuham.urbaneats.presentation.navigation.DetailsRoute
import com.shuham.urbaneats.presentation.navigation.HomeRoute
import com.shuham.urbaneats.presentation.navigation.LoginRoute
import com.shuham.urbaneats.presentation.navigation.MainAppRoute
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

                        // 1. SPLASH
                        composable<SplashRoute> {
                            SplashRoute(
                                onNavigateToHome = {
                                    // Navigate to the Main Container, not just Home
                                    navController.navigate(MainAppRoute) {
                                        popUpTo(SplashRoute) { inclusive = true }
                                    }
                                },
                                onNavigateToLogin = {
                                    navController.navigate(LoginRoute) {
                                        popUpTo(SplashRoute) { inclusive = true }
                                    }
                                }
                            )
                        }

                        // 2. LOGIN
                        composable<LoginRoute> {
                            LoginRoute(
                                onNavigateToHome = {
                                    navController.navigate(MainAppRoute) {
                                        popUpTo(LoginRoute) { inclusive = true }
                                    }
                                },
                                onNavigateToSignUp = { /* ... */ }
                            )
                        }

                        // 3. MAIN APP CONTAINER (Holds Bottom Bar)
                        composable<MainAppRoute> {
                            MainScreen(
                                onLogout = {
                                    navController.navigate(LoginRoute) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            )
                        }
                    }
                }


            }
        }
    }
}
