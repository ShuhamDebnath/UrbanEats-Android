package com.shuham.urbaneats.presentation.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Fastfood
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.shuham.urbaneats.R

@Composable
fun SplashScreen() {
    // Animation State
    val scale = remember { Animatable(0f) }

    // Trigger Animation
    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary), // Theme Primary (Brand Orange)
        contentAlignment = Alignment.Center
    ) {
        // Center Logo Section
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.scale(scale.value) // Apply animation here
        ) {
            // White Background Circle for Logo Contrast
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.background, // Force white background for logo visibility
                shadowElevation = 10.dp, // Add depth
                modifier = Modifier.size(140.dp) // Container Size
            ) {
                Box(contentAlignment = Alignment.Center) {
                    AsyncImage(
                        model = R.drawable.ic_all_food, // Your drawable resource
                        contentDescription = "Logo",
                        modifier = Modifier.size(90.dp), // Logo Size (smaller than container)
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

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