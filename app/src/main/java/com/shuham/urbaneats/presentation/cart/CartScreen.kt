package com.shuham.urbaneats.presentation.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.shuham.urbaneats.data.local.entity.CartItemEntity
import org.koin.androidx.compose.koinViewModel

// 1. ROUTE
@Composable
fun CartRoute(
    onCheckoutClick: () -> Unit,
    viewModel: CartViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    CartScreen(
        state = state,
        onIncrement = viewModel::incrementQuantity,
        onDecrement = viewModel::decrementQuantity,
        onCheckout = onCheckoutClick
    )
}

// 2. SCREEN
@Composable
fun CartScreen(
    state: CartState,
    onIncrement: (String, Int) -> Unit,
    onDecrement: (String, Int) -> Unit,
    onCheckout: () -> Unit
) {
    Scaffold(
        bottomBar = {
            if (state.summary.items.isNotEmpty()) {
                CartBottomBar(total = state.summary.totalPrice, onCheckout = onCheckout)
            }
        }
    ) { innerPadding ->
        if (state.summary.items.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Your cart is empty", style = MaterialTheme.typography.headlineSmall)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    top = innerPadding.calculateTopPadding() + 16.dp,
                    bottom = innerPadding.calculateBottomPadding() + 16.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.summary.items) { item ->
                    CartItemRow(item, onIncrement, onDecrement)
                }
            }
        }
    }
}

// 3. COMPONENTS
@Composable
fun CartItemRow(
    item: CartItemEntity,
    onIncrement: (String, Int) -> Unit,
    onDecrement: (String, Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image
            AsyncImage(
                model = item.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, fontWeight = FontWeight.Bold, maxLines = 1)
                Text("$${item.price}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            }

            // Quantity Controls
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { onDecrement(item.productId, item.quantity) },
                    modifier = Modifier.size(32.dp).background(Color.LightGray.copy(alpha = 0.2f), androidx.compose.foundation.shape.CircleShape)
                ) {
                    Icon(
                        imageVector = if(item.quantity == 1) Icons.Default.Delete else Icons.Default.Remove,
                        contentDescription = "Remove",
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "${item.quantity}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { onIncrement(item.productId, item.quantity) },
                    modifier = Modifier.size(32.dp).background(MaterialTheme.colorScheme.primary, androidx.compose.foundation.shape.CircleShape)
                ) {
                    Icon(Icons.Default.Add, "Add", tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun CartBottomBar(total: Double, onCheckout: () -> Unit) {
    Surface(
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Total", style = MaterialTheme.typography.titleLarge, color = Color.Gray)
                Text(
                    "$${String.format("%.2f", total)}",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onCheckout,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Checkout", fontSize = 18.sp)
            }
        }
    }
}