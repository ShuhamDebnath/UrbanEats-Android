package com.shuham.urbaneats.presentation.checkout

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 1. ROUTE
@Composable
fun OrderSuccessRoute(
    onHomeClick: () -> Unit,
    onTrackClick: () -> Unit
) {
    // UX Rule: When on Success screen, pressing "Back" should go Home, not back to Checkout.
    BackHandler {
        onHomeClick()
    }

    OrderSuccessScreen(
        onTrackClick = onTrackClick,
        onHomeClick = onHomeClick
    )
}

// 2. SCREEN
@Composable
fun OrderSuccessScreen(
    onTrackClick: () -> Unit,
    onHomeClick: () -> Unit
) {
    // Animation State: Starts at 0 (invisible/tiny)
    val scale = remember { Animatable(0f) }

    // Trigger Animation on Entry
    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy, // Bouncy effect
                stiffness = Spring.StiffnessLow // Slower, more visible bounce
            )
        )
    }

    Scaffold(
        containerColor = Color(0xFFF5F5F5)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Push content to center
            Spacer(modifier = Modifier.weight(1f))

            // Success Icon Container (Now Animated)
            Box(
                modifier = Modifier
                    .scale(scale.value) // <--- APPLY ANIMATION HERE
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFBE9E7)), // Very Light Orange/Peach ring
                contentAlignment = Alignment.Center
            ) {
                // Inner Circle
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFCCBC)), // Light Orange
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Success",
                        modifier = Modifier.size(50.dp),
                        tint = Color(0xFFE65100) // Dark Orange Check
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Order Placed!",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "You're about to eat well.\nOrder ID #123456",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            // Push Button to bottom
            Spacer(modifier = Modifier.weight(1f))

            // Buttons Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Primary Action: Track
                Button(
                    onClick = onTrackClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(50), // Pill shape
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100))
                ) {
                    Text("Track Order", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                // Secondary Action: Home (Text Button for clean look)
                TextButton(
                    onClick = onHomeClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text("Back to Home", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


@Preview
@Composable
private fun OrderSuccessScreenPrev() {
    OrderSuccessScreen(
        onTrackClick = {},
        onHomeClick = {}
    )

}