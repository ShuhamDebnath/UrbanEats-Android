package com.shuham.urbaneats.presentation.cart

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    state: CartState,
    onIncrement: (String, Int) -> Unit,
    onDecrement: (String, Int) -> Unit,
    onCheckout: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Cart", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            if (state.summary.items.isNotEmpty()) {
                CartSummaryFooter(total = state.summary.totalPrice, onCheckout = onCheckout)
            }
        }
    ) { innerPadding ->
        if (state.summary.items.isEmpty()) {
            // ENHANCED EMPTY STATE
            Box(
                modifier = Modifier.padding(innerPadding).fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(100.dp),
                        tint = Color.LightGray.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("Your cart is empty", style = MaterialTheme.typography.headlineSmall, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Go find some cravings!", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    Spacer(modifier = Modifier.height(24.dp))
                    // Optional: You can add a button here to navigate back to Home if navigation was passed down
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    top = innerPadding.calculateTopPadding() + 16.dp,
                    bottom = innerPadding.calculateBottomPadding() + 16.dp,
                    start = 24.dp,
                    end = 24.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.summary.items) { item ->
                    CartItemCard(item, onIncrement, onDecrement)
                }
            }
        }
    }
}

// 3. PREMIUM ITEM CARD
@Composable
fun CartItemCard(
    item: CartItemEntity,
    onIncrement: (String, Int) -> Unit,
    onDecrement: (String, Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp), ambientColor = Color.Gray.copy(0.05f), spotColor = Color.Gray.copy(0.05f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$${item.price}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Qty Controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(50))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Icon(
                    imageVector = if (item.quantity == 1) Icons.Default.DeleteOutline else Icons.Default.Remove,
                    contentDescription = "Remove",
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(50))
                        .clickable { onDecrement(item.productId, item.quantity) }
                        .padding(4.dp),
                    tint = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "${item.quantity}",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(50))
                        .clickable { onIncrement(item.productId, item.quantity) }
                        .padding(4.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

// 4. COLLAPSIBLE PREMIUM FOOTER
@Composable
fun CartSummaryFooter(total: Double, onCheckout: () -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }
    // FIX: State for the text field
    var promoCode by remember { mutableStateOf("") }

    val rotationState by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f, label = "Rotation")

    // We use the Surface to draw the background
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        shadowElevation = 16.dp,
        modifier = Modifier.fillMaxWidth() // Ensure it takes width
    ) {
        Column(modifier = Modifier.padding(24.dp)) {

            // 1. Header Row (Total + Expand Icon)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                    val finalTotal = total + 2.00 + (total * 0.05)
                    Text(
                        "$${String.format("%.2f", finalTotal)}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(if(isExpanded) "Hide Details" else "View Bill", color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Toggle Details",
                        modifier = Modifier.rotate(rotationState),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Expandable Details (Bill Breakdown + Promo)
            AnimatedVisibility(visible = isExpanded) {
                Column {
                    // Promo Code - FIXED: Now uses 'promoCode' state
                    OutlinedTextField(
                        value = promoCode,
                        onValueChange = { promoCode = it },
                        placeholder = { Text("Promo Code") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.background,
                            focusedContainerColor = MaterialTheme.colorScheme.background,
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = MaterialTheme.colorScheme.primary
                        ),
                        trailingIcon = {
                            Button(
                                onClick = { /* TODO: Validate Code Logic */ },
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                modifier = Modifier.padding(end = 4.dp).height(36.dp)
                            ) {
                                Text("Apply")
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Bill Rows
                    BillRow(label = "Subtotal", amount = total)
                    BillRow(label = "Delivery Fee", amount = 2.00)
                    BillRow(label = "Tax (5%)", amount = total * 0.05)

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        thickness = DividerDefaults.Thickness,
                        color = Color.LightGray.copy(0.3f)
                    )
                }
            }

            // 3. Checkout Button (Always Visible)
            Button(
                onClick = onCheckout,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Checkout", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun BillRow(label: String, amount: Double) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.Gray)
        Text("$${String.format("%.2f", amount)}", fontWeight = FontWeight.SemiBold)
    }
}