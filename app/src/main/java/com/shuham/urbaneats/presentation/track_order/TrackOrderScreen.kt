package com.shuham.urbaneats.presentation.track_order

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel


@Composable
fun TrackOrderRoute(
    onBackClick: () -> Unit,
    viewModel: TrackOrderViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    TrackOrderScreen(state = state, onBackClick = onBackClick)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackOrderScreen(
    state: TrackOrderState,
    onBackClick: () -> Unit
) {
    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        topBar = {
            CenterAlignedTopAppBar(
                // Display ID in Title
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Track Order", fontWeight = FontWeight.Bold)
                        Text(
                            "ID: #${state.orderId.takeLast(6).uppercase()}",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFFF5F5F5)
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp)
        ) {
            // ... (Rest of the UI: Estimated Card, Timeline, Driver Card remains the same) ...
            // 1. Estimated Time Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Estimated Delivery", color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = state.estimatedTime,
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE65100)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 2. Timeline
            Text("Order Status", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))

            TimelineStep(
                title = "Order Placed",
                subtitle = "We have received your order",
                isActive = state.currentStep >= 1,
                isCompleted = state.currentStep > 1,
                isLast = false
            )
            TimelineStep(
                title = "Preparing",
                subtitle = "Your food is being cooked",
                isActive = state.currentStep >= 2,
                isCompleted = state.currentStep > 2,
                isLast = false
            )
            TimelineStep(
                title = "Out for Delivery",
                subtitle = "Rider picked up your order",
                isActive = state.currentStep >= 3,
                isCompleted = state.currentStep > 3,
                isLast = false
            )
            TimelineStep(
                title = "Delivered",
                subtitle = "Enjoy your meal!",
                isActive = state.currentStep >= 4,
                isCompleted = state.currentStep > 4,
                isLast = true
            )

            Spacer(modifier = Modifier.weight(1f))

            // 3. Driver Card
            if (state.currentStep >= 3) {
                DriverCard()
            }
        }
    }
}

// ... (Keep TimelineStep and DriverCard components) ...
@Composable
fun TimelineStep(
    title: String,
    subtitle: String,
    isActive: Boolean,
    isCompleted: Boolean,
    isLast: Boolean
) {
    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        // Indicator Column
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // The Dot
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            isCompleted -> Color(0xFF4CAF50) // Green
                            isActive -> Color(0xFFE65100)    // Orange
                            else -> Color.LightGray
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isCompleted) {
                    Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }

            // The Line (Only if not last)
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .fillMaxHeight() // Fills height to next item
                        .background(if (isCompleted) Color(0xFF4CAF50) else Color.LightGray)
                        .padding(vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Text Column
        Column(modifier = Modifier.padding(bottom = 32.dp)) {
            Text(
                text = title,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                color = if (isActive) Color.Black else Color.Gray
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun DriverCard() {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFFFFF3E0),
                modifier = Modifier.size(50.dp)
            ) {
                Icon(Icons.Default.Person, null, modifier = Modifier.padding(12.dp), tint = Color(0xFFE65100))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text("John Doe", fontWeight = FontWeight.Bold)
                Text("Your Rider", color = Color.Gray, fontSize = 12.sp)
            }

            IconButton(
                onClick = { /* Call Action */ },
                modifier = Modifier.background(Color(0xFFE65100), CircleShape)
            ) {
                Icon(Icons.Default.Call, null, tint = Color.White)
            }
        }
    }
}