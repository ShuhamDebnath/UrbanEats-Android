package com.shuham.urbaneats.presentation.checkout

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import org.koin.androidx.compose.koinViewModel

@Composable
fun CheckoutRoute(
    onOrderSuccess: (String) -> Unit,
    onOrderFailure: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: CheckoutViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CheckoutEffect.NavigateToSuccess -> onOrderSuccess(effect.orderId)
                is CheckoutEffect.NavigateToFailure -> onOrderFailure(effect.reason)
                is CheckoutEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    CheckoutScreen(
        state = state,
        onAddressChange = viewModel::onAddressChange,
        onPlaceOrder = viewModel::placeOrder,
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    state: CheckoutState,
    onAddressChange: (String) -> Unit,
    onPlaceOrder: () -> Unit,
    onBackClick: () -> Unit
) {
    // Local state for UI selection (Dummy for now)
    var selectedDelivery by remember { mutableStateOf("Priority") }
    var selectedPayment by remember { mutableStateOf("Visa") }

    // Dialog State
    var showAddressDialog by remember { mutableStateOf(false) }

    // 1. ADDRESS INPUT DIALOG
    if (showAddressDialog) {
        var tempAddress by remember { mutableStateOf(state.address) }
        AlertDialog(
            onDismissRequest = { showAddressDialog = false },
            title = { Text("Delivery Address") },
            text = {
                OutlinedTextField(
                    value = tempAddress,
                    onValueChange = { tempAddress = it },
                    label = { Text("Enter full address") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onAddressChange(tempAddress) // Update ViewModel
                        showAddressDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100))
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddressDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = Color.White
        )
    }


    Scaffold(
        containerColor = Color(0xFFF5F5F5), // Light Gray Background
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Checkout", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF5F5F5)
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onPlaceOrder,
                    enabled = !state.isLoading && state.address.isNotBlank(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100))
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        // Calculate Total including delivery
                        val deliveryFee = if(selectedDelivery == "Priority") 2.00 else 0.00
                        val total = state.summary.totalPrice + deliveryFee
                        Text("Place Order - $${String.format("%.2f", total)}", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 16.dp,
                bottom = innerPadding.calculateBottomPadding() + 16.dp,
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. Delivery Address Section
            item {
                SectionHeader("Delivery Address", "Edit") { showAddressDialog = true }
                Spacer(modifier = Modifier.height(12.dp))

                // Show ACTUAL state address or a prompt to add one
                val displayAddress = if (state.address.isBlank()) "Add Delivery Address" else state.address
                val textColor = if (state.address.isBlank()) Color(0xFFE65100) else Color.Gray

                AddressCard(
                    address = displayAddress,
                    textColor = textColor,
                    onClick = { showAddressDialog = true }
                )

                if (state.error != null) {
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // 2. Delivery Options Section
            item {
                Text("Delivery Options", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))

                DeliveryOptionCard(
                    title = "Priority",
                    subtitle = "15-20 min",
                    price = "+$2.00",
                    isSelected = selectedDelivery == "Priority",
                    onClick = { selectedDelivery = "Priority" }
                )
                Spacer(modifier = Modifier.height(12.dp))
                DeliveryOptionCard(
                    title = "Standard",
                    subtitle = "30-40 min",
                    price = "Free",
                    isSelected = selectedDelivery == "Standard",
                    onClick = { selectedDelivery = "Standard" }
                )
            }

            // 3. Payment Method Section
            item {
                Text("Payment Method", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))

                PaymentMethodCard(
                    iconUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5e/Visa_Inc._logo.svg/2560px-Visa_Inc._logo.svg.png", // Visa Logo URL
                    title = "Visa •••• 1234",
                    isSelected = selectedPayment == "Visa",
                    onClick = { selectedPayment = "Visa" }
                )
                Spacer(modifier = Modifier.height(12.dp))
                PaymentMethodCard(
                    iconUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/31/Apple_logo_white.svg/1200px-Apple_logo_white.svg.png", // Apple Logo (needs dark bg usually, mocking here)
                    title = "Apple Pay",
                    isSelected = selectedPayment == "Apple",
                    isDarkIcon = true,
                    onClick = { selectedPayment = "Apple" }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Add New Method Button
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFE0B2)), // Light Orange
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, null, tint = Color(0xFFE65100))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add New Method", color = Color(0xFFE65100), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// --- COMPONENTS ---

@Composable
fun SectionHeader(title: String, actionText: String, onAction: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(
            text = actionText,
            color = Color(0xFFE65100),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.clickable { onAction() }
        )
    }
}


@Composable
fun AddressCard(address: String, textColor: Color = Color.Gray, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Map Placeholder Image
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF0F0F0))
            ) {
                Icon(Icons.Default.LocationOn, null, modifier = Modifier.align(Alignment.Center), tint = Color.Gray)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text("Home", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = address,
                    color = textColor,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = if (textColor == Color.Gray) FontWeight.Normal else FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun DeliveryOptionCard(title: String, subtitle: String, price: String, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFFFF3E0) else Color.White),
        border = if (isSelected) BorderStroke(1.5.dp, Color(0xFFE65100)) else BorderStroke(0.dp, Color.Transparent),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if(isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                    contentDescription = null,
                    tint = if(isSelected) Color(0xFFE65100) else Color.LightGray
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(title, fontWeight = FontWeight.Bold)
                    Text(subtitle, color = if(isSelected) Color(0xFFE65100) else Color.Gray, fontSize = 14.sp)
                }
            }
            Text(price, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PaymentMethodCard(iconUrl: String, title: String, isSelected: Boolean, isDarkIcon: Boolean = false, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = if (isSelected) BorderStroke(1.5.dp, Color(0xFFE65100)) else BorderStroke(0.dp, Color.Transparent),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if(isDarkIcon) Color.Black else Color.White,
                    border = BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.3f)),
                    modifier = Modifier.size(40.dp, 28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (isDarkIcon) {
                            Icon(androidx.compose.material.icons.Icons.Default.CreditCard, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        } else {
                            Text("VISA", fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.Blue)
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))
                Text(title, fontWeight = FontWeight.Medium)
            }

            Icon(
                imageVector = if(isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = if(isSelected) Color(0xFFE65100) else Color.LightGray
            )
        }
    }
}