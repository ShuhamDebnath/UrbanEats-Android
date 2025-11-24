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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Remove
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
import com.shuham.urbaneats.domain.model.Product
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
        containerColor = Color(0xFFF5F5F5), // Light Gray Background from screenshot
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Cart", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF5F5F5)
                ),
                actions = {
                    Text(
                        text = "Clear all",
                        color = Color(0xFFE65100), // Orange
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clickable { onClearAll() }
                    )
                },
                navigationIcon = {
                    // Optional Back Icon
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, null, modifier = Modifier.padding(start = 16.dp))
                }
            )
        },
        bottomBar = {
            if (state.summary.items.isNotEmpty()) {
                // Sticky Button Container
                Surface(
                    color = Color.White,
                    shadowElevation = 16.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onCheckout,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .height(56.dp),
                        shape = RoundedCornerShape(50), // Fully rounded pill
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100))
                    ) {
                        Text("Proceed to Checkout", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        if (state.summary.items.isEmpty()) {
            // Empty State
            Box(modifier = Modifier.padding(innerPadding).fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Your cart is empty", color = Color.Gray)
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
                        fontWeight = FontWeight.Bold
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

// 3. ITEM CARD (Fixed Price Logic)
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

                // Show selected options if they exist (Added bonus fix)
                if (item.selectedOptions.isNotBlank()) {
                    Text(
                        text = item.selectedOptions,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // FIX: Calculate Total Line Price (Unit Price * Quantity)
                val lineTotal = item.price * item.quantity

                Text(
                    text = "$${String.format("%.2f", lineTotal)}",
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

// 4. UPSELL SECTION (Horizontal Scroll)
@Composable
fun UpsellSection(onAdd: (Product) -> Unit) {
    // Hardcoded upsell items mapped to Domain Product objects
    // IDs should ideally be unique strings
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
                colors = CardDefaults.cardColors(containerColor = Color.White),
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
                    Text(product.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1)
                    Text("$${product.price}", color = Color.Gray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { onAdd(product) }, // <--- TRIGGER ADD
                        modifier = Modifier.fillMaxWidth().height(32.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFE0B2)),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Add", color = Color(0xFFE65100), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// 5. BILL CARD (Expandable)
@Composable
fun CartBillCard(total: Double) {
    var isExpanded by remember { mutableStateOf(false) }
    var promoCode by remember { mutableStateOf("") }
    val rotationState by animateFloatAsState(targetValue = if (isExpanded) 180f else 0f, label = "Rotation")

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            AnimatedVisibility(visible = isExpanded) {
                Column {
                    OutlinedTextField(
                        value = promoCode, onValueChange = { promoCode = it }, placeholder = { Text("Promo Code") },
                        modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF5F5F5), focusedContainerColor = Color(0xFFF5F5F5),
                            unfocusedBorderColor = Color.Transparent, focusedBorderColor = Color(0xFFE65100)
                        ),
                        trailingIcon = { Button(onClick = {}, contentPadding = PaddingValues(horizontal = 16.dp), modifier = Modifier.padding(end = 4.dp).height(36.dp)) { Text("Apply") } }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    BillRow("Subtotal", total)
                    BillRow("Delivery Fee", 5.00)
                    BillRow("Tax (10%)", total * 0.10)
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        thickness = 1.dp,
                        color = Color.LightGray.copy(alpha = 0.3f)
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().clickable { isExpanded = !isExpanded },
                horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Total", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.KeyboardArrowDown, "Toggle", modifier = Modifier.rotate(rotationState), tint = Color.Gray)
                }
                val finalTotal = total + 5.00 + (total * 0.10)
                Text("$${String.format("%.2f", finalTotal)}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
            }
        }
    }
}

@Composable
fun BillRow(label: String, amount: Double) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text("$${String.format("%.2f", amount)}", fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}