package com.shuham.urbaneats.presentation.cart

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
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
import com.shuham.urbaneats.domain.model.Product
import com.shuham.urbaneats.ui.theme.UrbanLightGray // Import if you want to use it, or use surfaceVariant
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
        onCheckout = onCheckoutClick,
        onClearAll = viewModel::clearAllCart,
        onAddUpsell = viewModel::addUpsellItem
    )
}

// 2. SCREEN
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    state: CartState,
    onIncrement: (String, Int) -> Unit,
    onDecrement: (String, Int) -> Unit,
    onCheckout: () -> Unit,
    onClearAll: () -> Unit,
    onAddUpsell: (Product) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background, // Theme-aware background
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "My Cart",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground // Theme-aware text
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background // Theme-aware background
                ),
                actions = {
                    Text(
                        text = "Clear all",
                        color = MaterialTheme.colorScheme.primary, // Theme Primary
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable { onClearAll() }
                    )
                },
//                navigationIcon = {
//                    Icon(
//                        Icons.AutoMirrored.Filled.ArrowBack,
//                        null,
//                        modifier = Modifier.padding(start = 16.dp),
//                        tint = MaterialTheme.colorScheme.onBackground // Theme-aware icon
//                    )
//                }
            )
        },
        bottomBar = {
            if (state.summary.items.isNotEmpty()) {
                // Sticky Button Container
                Surface(
                    color = MaterialTheme.colorScheme.surface, // Theme Surface
                    shadowElevation = 16.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onCheckout,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(56.dp),
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary) // Theme Primary
                    ) {
                        Text(
                            "Proceed to Checkout",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary // Theme OnPrimary
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        if (state.summary.items.isEmpty()) {
            // Empty State
            Box(modifier = Modifier.padding(innerPadding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(100.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f) // Theme-aware gray
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "Your cart is empty",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant // Theme-aware gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Go find some cravings!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant // Theme-aware gray
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    top = innerPadding.calculateTopPadding() + 16.dp,
                    bottom = innerPadding.calculateBottomPadding() + 16.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // 1. Cart Items
                items(state.summary.items) { item ->
                    CartItemCard(item, onIncrement, onDecrement)
                }

                // 2. Upsell Section (Complete your meal)
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Complete your meal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground // Theme-aware text
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    UpsellSection(onAdd = onAddUpsell)
                }

                // 3. Bill Details Card
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    CartBillCard(total = state.summary.totalPrice)
                }
            }
        }
    }
}

@Composable
fun UpsellSection(onAdd: (Product) -> Unit) {
    val upsellItems = listOf(
        Product(
            id = "upsell_coke",
            name = "Coca-Cola",
            description = "Chilled Can",
            price = 2.50,
            imageUrl = "https://images.unsplash.com/photo-1622483767028-3f66f32aef97?w=500",
            rating = 4.5,
            category = "Drinks"
        ),
        Product(
            id = "upsell_brownie",
            name = "Brownie",
            description = "Chocolate Fudge",
            price = 4.00,
            imageUrl = "https://images.unsplash.com/photo-1564355808539-22fda35bed7e?w=500",
            rating = 4.8,
            category = "Dessert"
        ),
        Product(
            id = "upsell_fries",
            name = "Fries",
            description = "Crispy Salted",
            price = 3.50,
            imageUrl = "https://images.unsplash.com/photo-1573080496987-a199f8cd0a58?w=500",
            rating = 4.6,
            category = "Sides"
        )
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(upsellItems) { product ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), // Theme Surface
                modifier = Modifier.width(140.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                            .align(Alignment.CenterHorizontally)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        product.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onSurface // Theme Text
                    )
                    Text(
                        "$${product.price}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant, // Theme Secondary Text
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { onAdd(product) },
                        modifier = Modifier.fillMaxWidth().height(32.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer), // Light Orange Container
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            "Add",
                            color = MaterialTheme.colorScheme.onPrimaryContainer, // Text on Container
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemCard(
    item: CartItemEntity,
    onIncrement: (String, Int) -> Unit,
    onDecrement: (String, Int) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), // Theme Surface
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth().shadow(
            4.dp,
            RoundedCornerShape(16.dp),
            ambientColor = MaterialTheme.colorScheme.onSurface.copy(0.05f),
            spotColor = MaterialTheme.colorScheme.onSurface.copy(0.05f)
        )
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant), // Placeholder color
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    color = MaterialTheme.colorScheme.onSurface // Theme Text
                )

                if (item.selectedOptions.isNotBlank()) {
                    Text(
                        text = item.selectedOptions,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, // Theme Secondary Text
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))
                val lineTotal = item.price * item.quantity
                Text(
                    "$${String.format("%.2f", lineTotal)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary // Theme Primary
                )
            }

            // Qty Controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(50)) // Theme surface variant
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
                    tint = MaterialTheme.colorScheme.onSurface // Theme icon color
                )
                Text(
                    "${item.quantity}",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    color = MaterialTheme.colorScheme.onSurface // Theme text color
                )
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    modifier = Modifier
                        .size(24.dp)
                        .clip(RoundedCornerShape(50))
                        .clickable { onIncrement(item.productId, item.quantity) }
                        .padding(4.dp),
                    tint = MaterialTheme.colorScheme.primary // Theme Primary
                )
            }
        }
    }
}

@Composable
fun CartBillCard(total: Double) {
    var isExpanded by remember { mutableStateOf(false) }
    var promoCode by remember { mutableStateOf("") }
    val rotationState by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f, label = "Rotation")

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), // Theme Surface
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            AnimatedVisibility(visible = isExpanded) {
                Column {
                    OutlinedTextField(
                        value = promoCode,
                        onValueChange = { promoCode = it },
                        placeholder = { Text("Promo Code", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            unfocusedBorderColor = Color.Transparent,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                        ),
                        trailingIcon = {
                            Button(
                                onClick = {},
                                contentPadding = PaddingValues(horizontal = 16.dp),
                                modifier = Modifier.padding(end = 4.dp).height(36.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("Apply", color = MaterialTheme.colorScheme.onPrimary)
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    BillRow("Subtotal", total)
                    BillRow("Delivery Fee", 5.00)
                    BillRow("Tax (10%)", total * 0.10)
                    Divider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                        thickness = 1.dp
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Total",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface // Theme Text
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        Icons.Default.KeyboardArrowUp,
                        "Toggle",
                        modifier = Modifier.rotate(rotationState),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant // Theme Icon
                    )
                }
                val finalTotal = total + 5.00 + (total * 0.10)
                Text(
                    "$${String.format("%.2f", finalTotal)}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary // Theme Primary
                )
            }
        }
    }
}

@Composable
fun BillRow(label: String, amount: Double) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp) // Theme Gray
        Text("$${String.format("%.2f", amount)}", fontWeight = FontWeight.Medium, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface) // Theme Text
    }
}