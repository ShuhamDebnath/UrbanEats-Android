package com.shuham.urbaneats.presentation.admin.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shuham.urbaneats.domain.model.Order
import com.shuham.urbaneats.ui.theme.UrbanGreen
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminOrdersScreen(
    viewModel: AdminOrdersViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val tabs = listOf("Pending", "Preparing", "Out for Delivery", "Delivered")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Incoming Orders", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.loadOrders() }) {
                        Icon(Icons.Default.Refresh, "Refresh")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // 1. Status Tabs
            PrimaryScrollableTabRow(
                selectedTabIndex = tabs.indexOf(state.selectedTab).coerceAtLeast(0),
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                edgePadding = 16.dp
            ) {
                tabs.forEach { tab ->
                    Tab(
                        selected = state.selectedTab == tab,
                        onClick = { viewModel.onTabSelected(tab) },
                        text = { Text(tab) }
                    )
                }
            }

            // 2. Orders List
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.filteredOrders) { order ->
                        AdminOrderCard(
                            order = order,
                            onStatusChange = { newStatus ->
                                viewModel.updateStatus(order.id, newStatus)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdminOrderCard(
    order: Order,
    onStatusChange: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Order #${order.id.takeLast(6)}", fontWeight = FontWeight.Bold)
                Text(order.date, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Details
            Text("Address: ${order.address}", style = MaterialTheme.typography.bodyMedium)
            Text("Total: $${order.total}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(8.dp))

            // Items
            order.items.forEach {
                Text("${it.quantity}x ${it.name}", fontSize = 14.sp, color = Color.DarkGray)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons (Dynamic based on current status)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                when(order.status) {
                    "Pending" -> {
                        Button(onClick = { onStatusChange("Preparing") }) { Text("Accept & Cook") }
                    }
                    "Preparing" -> {
                        Button(onClick = { onStatusChange("Out for Delivery") }) { Text("Ship Order") }
                    }
                    "Out for Delivery" -> {
                        Button(
                            onClick = { onStatusChange("Delivered") },
                            colors = ButtonDefaults.buttonColors(containerColor = UrbanGreen)
                        ) { Text("Mark Delivered") }
                    }
                    "Delivered" -> {
                        Text("Completed", color = UrbanGreen, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}