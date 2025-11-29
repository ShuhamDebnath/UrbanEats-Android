package com.shuham.urbaneats.presentation.checkout

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
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
import com.shuham.urbaneats.ui.theme.UrbanGreen

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
    val scale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        )
    }

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

            // Success Icon Container
            Box(
                modifier = Modifier
                    .scale(scale.value)
                    .size(140.dp)
                    .clip(CircleShape)
                    // Outer ring: Surface Variant (Light Gray / Dark Gray)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                // Inner Circle
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary), // Theme Surface
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Success",
                        modifier = Modifier.size(50.dp),
                        tint = UrbanGreen // Semantic Green (Always Green)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Order Placed!",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground // Theme Text
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "You're about to eat well.\nOrder ID #123456",
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
                // Primary Action
                Button(
                    onClick = onTrackClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary, // Theme Orange
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Track Order", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }

                // Secondary Action
                OutlinedButton(
                    onClick = onHomeClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant), // Theme Outline
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text("Back to Home", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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