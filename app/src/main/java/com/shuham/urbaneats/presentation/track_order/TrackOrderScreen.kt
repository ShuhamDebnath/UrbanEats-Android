package com.shuham.urbaneats.presentation.track_order

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF5F5F5)),
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
                .verticalScroll(rememberScrollState()) // Allow scrolling if details are long
                .padding(24.dp)
        ) {
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

            Spacer(modifier = Modifier.height(24.dp))

            // NEW: Order Details Card (Address & Items)
            if (state.orderDetails != null) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Address Section
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, null, tint = Color(0xFFE65100), modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("Delivery Address", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(
                                    text = state.orderDetails.address,
                                    color = Color.Gray,
                                    fontSize = 12.sp,
                                    maxLines = 2
                                )
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(0.3f))

                        // Items Summary
                        state.orderDetails.items.forEach { item ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${item.quantity}x ${item.name}", fontSize = 14.sp, color = Color.DarkGray)
                                Text("$${String.format("%.2f", item.price * item.quantity)}", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Total
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Total", fontWeight = FontWeight.Bold)
                            Text("$${String.format("%.2f", state.orderDetails.total)}", fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            // 2. Timeline
            Text("Order Status", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))

            TimelineStep("Order Placed", "We have received your order", state.currentStep >= 1, state.currentStep > 1, false)
            TimelineStep("Preparing", "Your food is being cooked", state.currentStep >= 2, state.currentStep > 2, false)
            TimelineStep("Out for Delivery", "Rider picked up your order", state.currentStep >= 3, state.currentStep > 3, false)
            TimelineStep("Delivered", "Enjoy your meal!", state.currentStep >= 4, state.currentStep > 4, true)

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Driver Card
            if (state.currentStep >= 3) {
                DriverCard()
            }

            // Extra padding for scroll
            Spacer(modifier = Modifier.height(40.dp))
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