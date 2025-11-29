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
// 2. SCREEN
@Composable
fun OrderFailureScreen(
    reason: String,
    onRetryClick: () -> Unit,
    onBackToCartClick: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background // Theme Background
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
                    // Outer Circle: Light Red in Light Mode, Muted Red in Dark Mode
                    .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)),
                contentAlignment = Alignment.Center
            ) {
                // Inner Circle
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        // Inner Circle: Stronger Error Container color
                        .background(MaterialTheme.colorScheme.errorContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close, // X Icon
                        contentDescription = "Failed",
                        modifier = Modifier.size(50.dp),
                        tint = MaterialTheme.colorScheme.error // Theme Error Color (Red)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Order Failed",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground // Theme Text
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Something went wrong with your order.\nReason: $reason",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant, // Theme Secondary Text
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.weight(1f))

            // Buttons Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Primary Action: Retry (Red Button)
                Button(
                    onClick = onRetryClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error, // Red Button
                        contentColor = MaterialTheme.colorScheme.onError // Text on Red
                    )
                ) {
                    Text("Retry", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                // Secondary Action: Back to Cart
                TextButton(
                    onClick = onBackToCartClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = "Back to Cart",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant // Theme Secondary Text
                    )
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