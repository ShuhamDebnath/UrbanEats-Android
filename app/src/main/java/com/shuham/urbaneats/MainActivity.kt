package com.shuham.urbaneats

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shuham.urbaneats.core.NotificationHelper
import com.shuham.urbaneats.presentation.login.LoginRoute
import com.shuham.urbaneats.presentation.main.MainScreen
import com.shuham.urbaneats.presentation.navigation.LoginRoute
import com.shuham.urbaneats.presentation.navigation.MainAppRoute
import com.shuham.urbaneats.presentation.navigation.SignUpRoute
import com.shuham.urbaneats.presentation.navigation.SplashRoute
import com.shuham.urbaneats.presentation.signup.SignUpRoute
import com.shuham.urbaneats.presentation.splash.SplashRoute
import com.shuham.urbaneats.ui.theme.UrbanEatsTheme
import org.koin.compose.KoinContext


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Initialize Channel (Ensure this is here)
        val notificationHelper = NotificationHelper(this)
        notificationHelper.createNotificationChannel()

        setContent {
            UrbanEatsTheme {
                KoinContext {
                    // 2. ASK FOR PERMISSION (Android 13+)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val permissionLauncher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.RequestPermission(),
                            onResult = { isGranted ->
                                // Optional: Log if granted or denied
                            }
                        )

                        LaunchedEffect(Unit) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        }
                    }
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
                                onNavigateToSignUp = {
                                    navController.navigate(SignUpRoute)
                                }
                            )
                        }
                        // 3. SIGNUP
                        composable<SignUpRoute> {
                            SignUpRoute(
                                onNavigateToHome = {
                                    navController.navigate(MainAppRoute) {
                                        popUpTo(SignUpRoute) { inclusive = true }
                                    }
                                },
                                onNavigateToLogin = {
                                    navController.navigate(LoginRoute)
                                }
                            )
                        }

                        // 4. MAIN APP CONTAINER (Holds Bottom Bar)
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
