package com.shuham.urbaneats.presentation.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Fastfood
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 1. ROUTE
//@Composable
//fun SplashRoute(
//    onNavigateToLogin: () -> Unit,
//    onNavigateToHome: () -> Unit,
//    viewModel: SplashViewModel = koinViewModel()
//) {
//    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
//
//    LaunchedEffect(isLoggedIn) {
//        when (isLoggedIn) {
//            true -> onNavigateToHome()
//            false -> onNavigateToLogin()
//            null -> { /* Still checking */ }
//        }
//    }
//
//    SplashScreen()
//}

// 2. SCREEN
@Composable
fun SplashScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary), // Theme Primary (Brand Orange)
        contentAlignment = Alignment.Center
    ) {
        // Center Logo Section
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Rounded.Fastfood,
                contentDescription = "Logo",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onPrimary // Theme OnPrimary (White/Contrast)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Urbaneats",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onPrimary, // Theme OnPrimary
                fontWeight = FontWeight.Bold,
                letterSpacing = (-1).sp
            )
        }

        // Bottom Loader Section
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onPrimary, // Theme OnPrimary
                modifier = Modifier.size(32.dp),
                strokeWidth = 3.dp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Deliciousness delivered.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f) // Theme OnPrimary with opacity
            )
        }
    }
}

@Preview
@Composable
private fun SplashScreenPrev() {
    SplashScreen()
}