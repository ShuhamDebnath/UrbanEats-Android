package com.shuham.urbaneats.presentation.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shuham.urbaneats.domain.model.Order
import org.koin.androidx.compose.koinViewModel

// 1. ROUTE
@Composable
fun OrdersRoute(
    onBackClick: () -> Unit,
    viewModel: OrdersViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    OrdersScreen(state = state, onBackClick = onBackClick, onRefresh = viewModel::fetchOrders)
}

// 2. SCREEN
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    state: OrdersState,
    onBackClick: () -> Unit,
    onRefresh: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Orders", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Default.Refresh, "Refresh")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.error != null) {
                Text(state.error, color = Color.Red, modifier = Modifier.align(Alignment.Center))
            } else if (state.orders.isEmpty()) {
                Text("No orders yet", color = Color.Gray, modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.orders) { order ->
                        OrderCard(order)
                    }
                }
            }
        }
    }
}

// 3. PREMIUM CARD
@Composable
fun OrderCard(order: Order) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp),
        // Add a subtle border for structure
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Date + Status
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Date: ${order.date}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )

                // Status Chip
                Surface(
                    color = if(order.status == "Delivered") Color(0xFFE8F5E9) else Color(0xFFFFF3E0),
                    shape = RoundedCornerShape(50),
                ) {
                    Text(
                        text = order.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if(order.status == "Delivered") Color(0xFF2E7D32) else Color(0xFFEF6C00),
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Items Summary
            order.items.forEach { item ->
                Row(modifier = Modifier.padding(vertical = 2.dp)) {
                    Text(
                        text = "${item.quantity}x ",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp
                    )
                    Text(
                        text = item.name,
                        maxLines = 1,
                        fontSize = 14.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color.LightGray.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(12.dp))

            // Footer: ID + Total
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "ID: ...${order.id.takeLast(6)}",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
                Text(
                    text = "$${String.format("%.2f", order.total)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}