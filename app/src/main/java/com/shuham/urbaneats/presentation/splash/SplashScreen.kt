package com.shuham.urbaneats.presentation.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Fastfood
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

// ==========================================
// 1. THE ROUTE (Logic Helper)
// ==========================================
@Composable
fun SplashRoute(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: SplashViewModel = koinViewModel()
) {
    // Collect the session state safely
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()

    // React to state changes
    LaunchedEffect(isLoggedIn) {
        when (isLoggedIn) {
            true -> onNavigateToHome()
            false -> onNavigateToLogin()
            null -> { /* Still loading/checking token... stay on splash */ }
        }
    }

    // Show the UI
    SplashScreen()
}

// ==========================================
// 2. THE SCREEN (UI Only)
// ==========================================
@Composable
fun SplashScreen() {
    // Define your Brand Gradient
    val bgGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF6200EA), // Deep Purple
            Color(0xFF9D46FF)  // Lighter Purple
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // App Logo (Placeholder)
            Icon(
                imageVector = Icons.Rounded.Fastfood, // Uses Material Icon
                contentDescription = "Logo",
                modifier = Modifier.size(100.dp),
                tint = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            // App Name
            Text(
                text = "UrbanEats",
                style = MaterialTheme.typography.displayMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}