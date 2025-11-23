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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.shuham.urbaneats.domain.model.Product
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

// ==========================================
// 1. ROUTE (The Brain)
// ==========================================
@Composable
fun DetailRoute(
    foodId: String, // Passed from Navigation
    onBackClick: () -> Unit,
    viewModel: DetailViewModel = koinViewModel()
) {
    // Trigger data fetch
    LaunchedEffect(foodId) { viewModel.loadProduct(foodId) }
    val state by viewModel.state.collectAsState()

    // Snack State
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        // We pass innerPadding to avoid overlapping with snackbar, though Box usually handles it.
        Box(modifier = Modifier.padding(innerPadding)) {
            DetailScreen(
                state = state,
                onBackClick = onBackClick,
                onAddToCart = {
                    viewModel.addToCart()
                    // Show Feedback
                    scope.launch {
                        snackbarHostState.showSnackbar("Added to Cart")
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
    onAddToCart: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val scrollState = rememberLazyListState()
    // Local state for options (Dummy for UI demo)
    var selectedSize by remember { mutableStateOf("Regular") }
    var extraCheese by remember { mutableStateOf(false) }
    var extraAvocado by remember { mutableStateOf(false) }
    var quantity by remember { mutableIntStateOf(1) }
    var specialInstructions by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {

        if (state.isLoading || state.product == null) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            val product = state.product

            // --- DYNAMIC PRICE CALCULATION ---
            val sizePrice = if (selectedSize == "Large") 3.00 else 0.00
            val cheesePrice = if (extraCheese) 1.50 else 0.00
            val avocadoPrice = if (extraAvocado) 2.00 else 0.00

            val singleItemTotal = product.price + sizePrice + cheesePrice + avocadoPrice
            val finalTotalPrice = singleItemTotal * quantity
            // ----------------------------------

            // 1. Background Image (Fixed)
            AsyncImage(
                model = product.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp) // Takes top portion
                    .align(Alignment.TopCenter)
            )

            // 2. Gradient Overlay for Header Icons visibility
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Black.copy(alpha = 0.6f), Color.Transparent)
                        )
                    )
            )

            // 3. Top Bar Icons (Absolute positioning)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
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
                        tint = if(product.isFavorite) Color.Red else Color.White
                    )
                }
            }

            // 4. The White Sheet (Scrollable Content)
            // We use a Box with top padding to simulate the sheet sliding up
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 280.dp) // Overlap the image
            ) {
                Surface(
                    color = Color.White,
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
                                    .background(Color.LightGray, RoundedCornerShape(50))
                            )
                        }

                        // Scrollable Content
                        LazyColumn(
                            state = scrollState,
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                            modifier = Modifier.weight(1f) // Takes remaining space above footer
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
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "$${product.price}",
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = Color(0xFFE65100), // Orange
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = product.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Gray,
                                    lineHeight = 20.sp
                                )
                                Text(
                                    text = "Read more",
                                    color = Color(0xFFE65100),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    modifier = Modifier.clickable { }
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                            }

                            // Choose Size
                            item {
                                SectionTitle("Choose Size")
                                SizeOption(
                                    label = "Regular",
                                    price = "Included",
                                    selected = selectedSize == "Regular",
                                    onClick = { selectedSize = "Regular" }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                SizeOption(
                                    label = "Large",
                                    price = "+ $3.00",
                                    selected = selectedSize == "Large",
                                    onClick = { selectedSize = "Large" }
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                            }

                            // Add Extras
                            item {
                                SectionTitle("Add Extras")
                                ExtraOption(
                                    label = "Extra Cheese",
                                    price = "+ $1.50",
                                    checked = extraCheese,
                                    onCheckedChange = { extraCheese = it }
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                ExtraOption(
                                    label = "Avocado",
                                    price = "+ $2.00",
                                    checked = extraAvocado,
                                    onCheckedChange = { extraAvocado = it }
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                            }

                            // Special Instructions
                            item {
                                SectionTitle("Special Instructions")
                                OutlinedTextField(
                                    value = specialInstructions,
                                    onValueChange = { specialInstructions = it },
                                    placeholder = { Text("Add a note (e.g., no onions)", color = Color.Gray) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        unfocusedContainerColor = Color(0xFFF9F9F9),
                                        focusedContainerColor = Color(0xFFF9F9F9),
                                        unfocusedBorderColor = Color.Transparent,
                                        focusedBorderColor = Color(0xFFE65100)
                                    )
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                            }
                        }

                        // 5. Sticky Footer (Qty + Add Button)
                        Surface(
                            shadowElevation = 16.dp,
                            color = Color.White
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .navigationBarsPadding(), // Avoid overlapping system nav bar
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Qty Selector
                                Surface(
                                    color = Color(0xFFF5F5F5),
                                    shape = RoundedCornerShape(50),
                                    modifier = Modifier.height(50.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp)
                                    ) {
                                        IconButton(onClick = { if(quantity > 1) quantity-- }) {
                                            Icon(Icons.Default.Remove, null, tint = Color.Black)
                                        }
                                        Text(
                                            text = "$quantity",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 18.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp)
                                        )
                                        IconButton(onClick = { quantity++ }) {
                                            Icon(Icons.Default.Add, null, tint = Color(0xFFE65100))
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.width(16.dp))

                                // UPDATED BUTTON with Calculated Price
                                Button(
                                    onClick = onAddToCart,
                                    modifier = Modifier.weight(1f).height(50.dp),
                                    shape = RoundedCornerShape(50),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100))
                                ) {
                                    Text(
                                        text = "Add to Cart - $${String.format("%.2f", finalTotalPrice)}",
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
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
fun SizeOption(label: String, price: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (selected) Color(0xFFE65100) else Color.LightGray.copy(alpha = 0.5f)),
        color = if (selected) Color(0xFFFFF3E0) else Color.White, // Light Orange vs White
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
                // Radio Circle
//                Icon(
//                    // Use a circle outline or filled circle
//                    imageVector = if(selected) Icons.Default.Favorite else Icons.Default.FavoriteBorder, // Hack: Use Radio Icon instead
//                    contentDescription = null,
//                    tint = if (selected) Color(0xFFE65100) else Color.Gray,
//                    modifier = Modifier.size(20.dp) // Ideally use RadioButton composable
//                )
                // Better: Use actual Radio Button visual
                RadioButton(selected = selected, onClick = null, colors = RadioButtonDefaults.colors(selectedColor = Color(0xFFE65100)))

                Spacer(modifier = Modifier.width(12.dp))
                Text(label, fontWeight = FontWeight.Medium)
            }
            Text(price, color = if(selected) Color(0xFFE65100) else Color.Gray, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun ExtraOption(label: String, price: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, if (checked) Color(0xFFE65100) else Color.LightGray.copy(alpha = 0.5f)),
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
                // Checkbox visual
                // Using a simple icon or built-in Checkbox
                // Checkbox(checked = checked, onCheckedChange = null, colors = CheckboxDefaults.colors(checkedColor = Color(0xFFE65100)))
                // Custom circle for clean look
                Surface(
                    shape = CircleShape,
                    border = BorderStroke(1.dp, if(checked) Color(0xFFE65100) else Color.Gray),
                    color = if(checked) Color(0xFFE65100) else Color.Transparent,
                    modifier = Modifier.size(20.dp)
                ) {
                    if(checked) Icon(Icons.Default.Add, null, tint = Color.White, modifier = Modifier.padding(2.dp))
                }

                Spacer(modifier = Modifier.width(12.dp))
                Text(label, fontWeight = FontWeight.Medium)
            }
            Text(price, fontWeight = FontWeight.Bold)
        }
    }
}
