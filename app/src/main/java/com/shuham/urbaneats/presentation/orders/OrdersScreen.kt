package com.shuham.urbaneats.presentation.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
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
import com.shuham.urbaneats.ui.theme.UrbanGreen
import org.koin.androidx.compose.koinViewModel

// 1. ROUTE
@Composable
fun OrdersRoute(
    onBackClick: () -> Unit,
    onOrderClick: (Order) -> Unit,
    viewModel: OrdersViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    OrdersScreen(
        state = state,
        onBackClick = onBackClick,
        onRefresh = viewModel::fetchOrders,
        onOrderClick = onOrderClick
    )
}

// 2. SCREEN
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    state: OrdersState,
    onBackClick: () -> Unit,
    onRefresh: () -> Unit,
    onOrderClick: (Order) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background, // Theme Background
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "My Orders",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground // Theme Text
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            "Back",
                            tint = MaterialTheme.colorScheme.onBackground // Theme Icon
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(
                            Icons.Default.Refresh,
                            "Refresh",
                            tint = MaterialTheme.colorScheme.onBackground // Theme Icon
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (state.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colorScheme.primary
                )
            } else if (state.error != null) {
                Text(
                    state.error,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (state.orders.isEmpty()) {
                Text(
                    "No orders yet",
                    color = MaterialTheme.colorScheme.onSurfaceVariant, // Theme Gray
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.orders) { order ->
                        OrderCard(order = order, onOrderClick = { onOrderClick(order) })
                    }
                }
            }
        }
    }
}

// 3. PREMIUM CARD
@Composable
fun OrderCard(
    order: Order,
    onOrderClick: () -> Unit
) {
    Card(
        onClick = onOrderClick ,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), // Theme Surface
        elevation = CardDefaults.cardElevation(0.dp),
        // Subtle border based on theme outline
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant // Theme Gray
                )

                // Status Chip
                val isDelivered = order.status == "Delivered"
                // Use UrbanGreen for success, Primary (Orange) for pending
                val statusColor = if(isDelivered) UrbanGreen else MaterialTheme.colorScheme.primary
                // Use SurfaceVariant or a light tint for background
                val statusBg = if(isDelivered) UrbanGreen.copy(alpha = 0.1f) else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)

                Surface(
                    color = statusBg,
                    shape = RoundedCornerShape(50),
                ) {
                    Text(
                        text = order.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = statusColor,
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
                        color = MaterialTheme.colorScheme.primary, // Theme Primary
                        fontSize = 14.sp
                    )
                    Text(
                        text = item.name,
                        maxLines = 1,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface, // Theme Text
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(
                Modifier,
                DividerDefaults.Thickness,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
            )
            // Theme Divider
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant // Theme Gray
                )
                Text(
                    text = "$${String.format("%.2f", order.total)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface // Theme Text
                )
            }
        }
    }
}