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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.shuham.urbaneats.domain.model.Address
import org.koin.androidx.compose.koinViewModel

@Composable
fun CheckoutRoute(
    onOrderSuccess: (String) -> Unit,
    onOrderFailure: (String) -> Unit,
    onNoInternet: () -> Unit,
    onEditAddress: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: CheckoutViewModel = koinViewModel(),

) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current


    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is CheckoutEffect.NavigateToSuccess -> onOrderSuccess(effect.orderId)
                is CheckoutEffect.NavigateToFailure -> onOrderFailure(effect.reason)
                is CheckoutEffect.NavigateToNoInternet -> onNoInternet()
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
        onBackClick = onBackClick,
        onEditAddress = onEditAddress
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    state: CheckoutState,
    onAddressChange: (Address) -> Unit,
    onPlaceOrder: () -> Unit,
    onBackClick: () -> Unit,
    onEditAddress: () -> Unit
) {
    // Local state for UI selection (Dummy for now)
    var selectedDelivery by remember { mutableStateOf("Priority") }
    var selectedPayment by remember { mutableStateOf("Visa") }


    val focusManager = LocalFocusManager.current

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Checkout",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
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
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        onPlaceOrder()
                    },
                    enabled = !state.isLoading && state.address != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary, // Theme Primary
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
                    )
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
                SectionHeader("Delivery Address", "Change") { onEditAddress() } // <--- Trigger Nav
                Spacer(modifier = Modifier.height(12.dp))

                val displayAddress = state.address ?: Address(label = "Address", fullAddress = "Add Address")
                val textColor = if (state.address == null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

                AddressCard(
                    address = displayAddress,
                    textColor = textColor,
                    onClick = { onEditAddress() } // Clicking card also triggers nav
                )
            }

            // 2. Delivery Options Section
            item {
                Text(
                    "Delivery Options",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
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
                Text(
                    "Payment Method",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(12.dp))

                PaymentMethodCard(
                    iconUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/5e/Visa_Inc._logo.svg/2560px-Visa_Inc._logo.svg.png",
                    title = "Visa •••• 1234",
                    isSelected = selectedPayment == "Visa",
                    onClick = { selectedPayment = "Visa" }
                )
                Spacer(modifier = Modifier.height(12.dp))
                PaymentMethodCard(
                    iconUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/3/31/Apple_logo_white.svg/1200px-Apple_logo_white.svg.png",
                    title = "Apple Pay",
                    isSelected = selectedPayment == "Apple",
                    isDarkIcon = true,
                    onClick = { selectedPayment = "Apple" }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer), // Light Orange
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Add New Method",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
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
        Text(
            title,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = actionText,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            modifier = Modifier.clickable { onAction() }
        )
    }
}


@Composable
fun AddressCard(address: Address, textColor: Color, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                    .background(MaterialTheme.colorScheme.primaryContainer) // Theme variant bg
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    null,
                    modifier = Modifier.align(Alignment.Center),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = address.label,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = address.fullAddress,
                    color = textColor,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    fontWeight = if (textColor == MaterialTheme.colorScheme.primary) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun DeliveryOptionCard(title: String, subtitle: String, price: String, isSelected: Boolean, onClick: () -> Unit) {
    // Dynamic Colors
    val containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    val iconColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        border = BorderStroke(1.5.dp, borderColor),
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
                    tint = iconColor
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text(
                        subtitle,
                        color = if(isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 14.sp
                    )
                }
            }
            Text(price, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun PaymentMethodCard(iconUrl: String, title: String, isSelected: Boolean, isDarkIcon: Boolean = false, onClick: () -> Unit) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    val iconColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.5.dp, borderColor),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Icon Placeholder
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if(isDarkIcon) Color.Black else Color.White,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
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
                Text(title, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
            }

            Icon(
                imageVector = if(isSelected) Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                contentDescription = null,
                tint = iconColor
            )
        }
    }
}