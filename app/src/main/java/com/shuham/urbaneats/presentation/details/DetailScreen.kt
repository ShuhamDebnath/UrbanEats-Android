package com.shuham.urbaneats.presentation.details

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.shuham.urbaneats.domain.model.AddonOption
import com.shuham.urbaneats.domain.model.Product
import com.shuham.urbaneats.domain.model.SizeOption
import com.shuham.urbaneats.presentation.details.SizeOption
import com.shuham.urbaneats.ui.theme.UrbanRed
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

// ==========================================
// 1. ROUTE (The Brain)
// ==========================================
@Composable
fun DetailRoute(
    foodId: String, // Passed from Navigation
    onGoToCart: () -> Unit,
    onBackClick: () -> Unit, viewModel: DetailViewModel = koinViewModel()
) {
    // Trigger data fetch
    LaunchedEffect(foodId) { viewModel.loadProduct(foodId) }
    val state by viewModel.state.collectAsState()

    // Snack State
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }) { innerPadding ->
        // We pass innerPadding to avoid overlapping with snackbar, though Box usually handles it.
        Box(modifier = Modifier.padding(innerPadding)) {
            DetailScreen(
                state = state,
                onBackClick = onBackClick,
                onAddToCart = { size, addons, qty, notes ->
                    viewModel.addToCart(size, addons, qty, notes)

                    // Show Snackbar with Action
                    scope.launch {
                        val result = snackbarHostState.showSnackbar(
                            message = "Added to Cart",
                            actionLabel = "View Cart", // <--- CLICKABLE ACTION
                            duration = SnackbarDuration.Short
                        )
                        if (result == SnackbarResult.ActionPerformed) {
                            onGoToCart() // <--- NAVIGATE
                        }
                    }
                },
                onToggleFavorite = viewModel::toggleFavorite
            )
        }
    }
}

// ==========================================
// 2. SCREEN (The Beauty)
// ==========================================

@Composable
fun DetailScreen(
    state: DetailState,
    onBackClick: () -> Unit,
    onAddToCart: (SizeOption, Set<AddonOption>, Int, String) -> Unit,
    onToggleFavorite: () -> Unit
) {
    val scrollState = rememberLazyListState()

    // Default states
    var selectedSize by remember(state.product) {
        mutableStateOf(state.product?.sizes?.firstOrNull() ?: SizeOption("Regular", 0.0))
    }
    var selectedAddons by remember { mutableStateOf(setOf<AddonOption>()) }
    var quantity by remember { mutableIntStateOf(1) }
    var specialInstructions by remember { mutableStateOf("") }

    // Logic to calculate total
    val basePrice = state.product?.price ?: 0.0
    val sizePrice = selectedSize.price
    val addonsPrice = selectedAddons.sumOf { it.price }
    val unitPrice = basePrice + sizePrice + addonsPrice
    val finalTotalPrice = unitPrice * quantity

    // Background stays black for the image area
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {

        if (state.isLoading || state.product == null) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            val product = state.product

            // 1. Background Image
            AsyncImage(
                model = product.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .align(Alignment.TopCenter)
            )

            // 2. Gradient Overlay
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Black.copy(alpha = 0.7f), Color.Transparent)
                        )
                    )
            )

            // 3. Top Bar Icons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    //.statusBarsPadding()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White)
                }

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = if(product.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        // Use Semantic Red for favorite, White for unselected (on dark image)
                        tint = if(product.isFavorite) UrbanRed else Color.White
                    )
                }
            }

            // 4. Content Sheet
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 280.dp)
            ) {
                Surface(
                    // Use Theme Surface (White in Light, Dark Grey in Dark)
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column {
                        // Drag Handle
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(4.dp)
                                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(50))
                            )
                        }

                        // Scrollable Content
                        LazyColumn(
                            state = scrollState,
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            // Title & Price
                            item {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = product.name,
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "$${product.price}",
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = MaterialTheme.colorScheme.primary, // Theme Primary
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = product.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 20.sp
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                            }

                            // DYNAMIC SIZES
                            if (state.product?.sizes?.isNotEmpty() == true) {
                                item {
                                    SectionTitle("Choose Size")
                                    state.product.sizes.forEach { size ->
                                        val isSelected = size == selectedSize
                                        val priceText = if (size.price == 0.0) "Included" else "+$${size.price}"

                                        SizeOption(
                                            label = size.name,
                                            price = priceText,
                                            selected = isSelected,
                                            onClick = { selectedSize = size }
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }

                            // DYNAMIC ADDONS
                            if (state.product?.addons?.isNotEmpty() == true) {
                                item {
                                    SectionTitle("Add Extras")
                                    state.product.addons.forEach { addon ->
                                        val isChecked = selectedAddons.contains(addon)

                                        ExtraOption(
                                            label = addon.name,
                                            price = "+$${addon.price}",
                                            checked = isChecked,
                                            onCheckedChange = {
                                                selectedAddons = if (isChecked) selectedAddons - addon else selectedAddons + addon
                                            }
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }

                            // Special Instructions
                            item {
                                SectionTitle("Special Instructions")
                                OutlinedTextField(
                                    value = specialInstructions,
                                    onValueChange = { specialInstructions = it },
                                    placeholder = { Text("Add a note (e.g., no onions)", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                                    modifier = Modifier.fillMaxWidth().height(100.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        // Theme-aware input colors
                                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = Color.Transparent,
                                        cursorColor = MaterialTheme.colorScheme.primary,
                                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }

                        // Sticky Footer
                        Surface(
                            shadowElevation = 16.dp,
                            color = MaterialTheme.colorScheme.surface // Theme Surface
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .navigationBarsPadding(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Qty Selector
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant, // Theme Variant
                                    shape = RoundedCornerShape(50),
                                    modifier = Modifier.height(50.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    ) {
                                        IconButton(onClick = { if(quantity > 1) quantity-- }) {
                                            Icon(Icons.Default.Remove, null, tint = MaterialTheme.colorScheme.onSurface)
                                        }
                                        Text(
                                            text = "$quantity",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            color = MaterialTheme.colorScheme.onSurface,
                                            modifier = Modifier.padding(horizontal = 8.dp)
                                        )
                                        IconButton(onClick = { quantity++ }) {
                                            Icon(Icons.Default.Add, null, tint = MaterialTheme.colorScheme.primary)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                // Add Button
                                Button(
                                    onClick = {
                                        onAddToCart(selectedSize, selectedAddons, quantity, specialInstructions)
                                    },
                                    modifier = Modifier.weight(1f).height(50.dp),
                                    shape = RoundedCornerShape(50),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary, // Theme Primary
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                ) {
                                    Text(
                                        text = "Add - $${String.format("%.2f", finalTotalPrice)}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- COMPONENTS ---

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
fun SizeOption(label: String, price: String, selected: Boolean, onClick: () -> Unit) {
    val borderColor = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
    val backgroundColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface

    Surface(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, borderColor),
        color = backgroundColor,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Radio Icon Simulation
                Icon(
                    imageVector = if(selected) Icons.Default.Circle else Icons.Outlined.Circle, // Using Heart as placeholder for Radio, standard radio logic is better visually usually
                    contentDescription = null,
                    tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = label,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = price,
                color = if(selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ExtraOption(label: String, price: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val borderColor = if (checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant

    Surface(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, borderColor),
        color = MaterialTheme.colorScheme.surface, // Extras usually keep white/surface bg even if checked, or light tint
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { onCheckedChange(!checked) }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Checkbox Visual
                Surface(
                    shape = CircleShape,
                    border = BorderStroke(1.dp, if(checked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant),
                    color = if(checked) MaterialTheme.colorScheme.primary else Color.Transparent,
                    modifier = Modifier.size(20.dp)
                ) {
                    if(checked) {
                        Icon(Icons.Default.Add, null, tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.padding(2.dp))
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = label,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            Text(
                text = price,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
