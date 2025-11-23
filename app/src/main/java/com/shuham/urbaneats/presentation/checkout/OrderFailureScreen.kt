package com.shuham.urbaneats.presentation.checkout

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 1. ROUTE
@Composable
fun OrderFailureRoute(
    reason: String,
    onRetryClick: () -> Unit,
    onBackToCartClick: () -> Unit
) {
    // Back button should go back to cart so they can fix/retry
    BackHandler {
        onBackToCartClick()
    }

    OrderFailureScreen(
        reason = reason,
        onRetryClick = onRetryClick,
        onBackToCartClick = onBackToCartClick
    )
}

// 2. SCREEN
@Composable
fun OrderFailureScreen(
    reason: String,
    onRetryClick: () -> Unit,
    onBackToCartClick: () -> Unit
) {
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

            Spacer(modifier = Modifier.weight(1f))

            // Failure Icon Container
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFEBEE)), // Very Light Red
                contentAlignment = Alignment.Center
            ) {
                // Inner Circle
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFCDD2)), // Light Red
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Error, // X Icon
                        contentDescription = "Failed",
                        modifier = Modifier.size(70.dp),
                        tint = Color(0xFFD32F2F) // Dark Red
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Order Failed",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Something went wrong with your order.\nReason: $reason",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            // Buttons Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Primary Action: Retry
                Button(
                    onClick = onRetryClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)) // Red Button
                ) {
                    Text("Retry", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                // Secondary Action: Back to Cart
                TextButton(
                    onClick = onBackToCartClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text("Back to Cart", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}


@Preview
@Composable
private fun OrderFailureScreenPrev() {
    OrderFailureScreen(
        reason = "Network Error",
        onRetryClick = {},
        onBackToCartClick = {}
    )
    
}