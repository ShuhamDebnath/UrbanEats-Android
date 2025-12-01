package com.shuham.urbaneats

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shuham.urbaneats.core.NetworkUtils
import com.shuham.urbaneats.core.NotificationHelper
import com.shuham.urbaneats.data.local.TokenManager
import com.shuham.urbaneats.presentation.admin.AdminMainScreen
import com.shuham.urbaneats.presentation.common.NoInternetScreen
import com.shuham.urbaneats.presentation.login.LoginRoute
import com.shuham.urbaneats.presentation.main.MainScreen
import com.shuham.urbaneats.presentation.navigation.AdminDashboardRoute
import com.shuham.urbaneats.presentation.navigation.LoginRoute
import com.shuham.urbaneats.presentation.navigation.MainAppRoute
import com.shuham.urbaneats.presentation.navigation.NoInternetRoute
import com.shuham.urbaneats.presentation.navigation.SignUpRoute
import com.shuham.urbaneats.presentation.navigation.SplashRoute
import com.shuham.urbaneats.presentation.signup.SignUpRoute
import com.shuham.urbaneats.presentation.splash.SplashDestination
import com.shuham.urbaneats.presentation.splash.SplashScreen
import com.shuham.urbaneats.presentation.splash.SplashViewModel
import com.shuham.urbaneats.ui.theme.UrbanEatsTheme
import org.koin.androidx.compose.koinViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. Initialize Channel (Ensure this is here)
        val notificationHelper = NotificationHelper(this)
        notificationHelper.createNotificationChannel()

        // Direct access to TokenManager to read theme before UI loads
        val tokenManager = TokenManager(applicationContext)

        setContent {

            // 1. Observe Theme Preference (Reactive)
            // Defaults to "system" if nothing is saved
            val themePreference = tokenManager.getTheme().collectAsState(initial = "system").value

            // 2. Calculate Logic
            val useDarkTheme = when (themePreference) {
                "light" -> false
                "dark" -> true
                else -> isSystemInDarkTheme() // Uses Android OS setting
            }



            UrbanEatsTheme(
                darkTheme = useDarkTheme,
                dynamicColor = false // Force our Brand Colors
            ) {

                // ASK FOR PERMISSION (Android 13+)
                GrantNotificationPermission()

                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = SplashRoute
                ) {

                    // 1. SPLASH
                    composable<SplashRoute> {
                        // Inject SplashViewModel here
                        val viewModel: SplashViewModel = koinViewModel()
                        val destination by viewModel.destination.collectAsStateWithLifecycle()

                        LaunchedEffect(destination) {
                            when(destination) {
                                is SplashDestination.Home -> navController.navigate(MainAppRoute) { popUpTo(SplashRoute) { inclusive = true } }
                                is SplashDestination.Admin -> navController.navigate(AdminDashboardRoute) { popUpTo(SplashRoute) { inclusive = true } }
                                is SplashDestination.Login -> navController.navigate(LoginRoute) { popUpTo(SplashRoute) { inclusive = true } }
                                else -> {}
                            }
                        }

                        SplashScreen()
                    }

                    // 2. LOGIN
                    composable<LoginRoute> {
                        LoginRoute(
                            onNavigateToHome = { // role -> // You need to update LoginRoute callback to accept role if you want instant redirect,
                                // OR simpler: Login always goes to a routing check.
                                // For now, let's assume Login saves to DataStore.
                                // We can navigate to SplashRoute to re-evaluate role!
                                navController.navigate(SplashRoute) { popUpTo(0) { inclusive = true } }
                            },
                            onNavigateToSignUp = {
                                navController.navigate(SignUpRoute)
                            }
                        )
                    }

                    // 3. SIGN UP
                    composable<SignUpRoute> {
                        // Ensure you created SignUpRoute in the previous step or import it
                        SignUpRoute(
                            onNavigateToHome = {
                                navController.navigate(MainAppRoute) {
                                    popUpTo(LoginRoute) { inclusive = true }
                                    popUpTo(SignUpRoute) { inclusive = true }
                                }
                            },
                            onNavigateToLogin = {
                                navController.popBackStack()
                            }
                        )
                    }

                    // 4. MAIN APP CONTAINER
                    composable<MainAppRoute> {
                        MainScreen(
                            onLogout = {
                                navController.navigate(LoginRoute) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }

                    // 5. ADMIN DASHBOARD (NEW)
                    composable<AdminDashboardRoute> {
                        // FIX: Use AdminMainScreen for the full bottom bar experience
                        AdminMainScreen(
                            onLogout = {
                                // You might want a VM here to clear session first
                                navController.navigate(LoginRoute) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        )
                    }




                    // 6. GLOBAL NO INTERNET SCREEN
                    composable<NoInternetRoute> {
                        NoInternetScreen(
                            onRetry = {
                                // THE LOGIC IS HERE
                                if (NetworkUtils.isNetworkAvailable(this@MainActivity)) {
                                    // Internet is back! Go back to previous screen
                                    navController.popBackStack()
                                } else {
                                    // Still no internet
                                    Toast.makeText(
                                        this@MainActivity,
                                        "Internet is still unavailable",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GrantNotificationPermission() {
    // ASK FOR PERMISSION (Android 13+)
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
}
