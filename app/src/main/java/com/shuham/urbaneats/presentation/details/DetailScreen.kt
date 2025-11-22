package com.shuham.urbaneats.presentation.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
                }
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
    onAddToCart: () -> Unit
) {
    val scrollState = rememberLazyListState()
    val headerHeight = 300.dp
    val headerHeightPx = with(LocalDensity.current) { headerHeight.toPx() }

    Box(modifier = Modifier.fillMaxSize()) {

        if (state.isLoading || state.product == null) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            val product = state.product

            LazyColumn(
                state = scrollState,
                modifier = Modifier.fillMaxSize().padding(bottom = 80.dp) // Space for bottom button
            ) {
                // 1. Header Placeholder (Invisible box to push content down)
                item { Spacer(modifier = Modifier.height(headerHeight)) }

                // 2. Content Body
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                            )
                            .padding(24.dp)
                    ) {
                        // Title & Price
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
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
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Rating
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, null, tint = Color(0xFFFFB300))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "${product.rating} (500+ reviews)", color = Color.Gray)
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text("Description", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = product.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray,
                            lineHeight = 24.sp
                        )
                    }
                }
            }

            // 3. Parallax Header Image (Overlay)
            // This sits ON TOP of the list but moves with it
            val scrollOffset = scrollState.firstVisibleItemScrollOffset
            val firstItemIndex = scrollState.firstVisibleItemIndex

            // Logic: If we scrolled past the first item, the offset is huge.
            // Otherwise, it's the scroll amount.
            val offset = if (firstItemIndex > 0) headerHeightPx else scrollOffset.toFloat()

            AsyncImage(
                model = product.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(headerHeight)
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .graphicsLayer {
                        // Parallax Effect: Move image up at half speed
                        translationY = -offset / 2f
                        alpha = 1f - (offset / headerHeightPx) // Fade out
                    }
            )

            // 4. Back Button (Always on top)
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .padding(top = 48.dp, start = 16.dp)
                    .align(Alignment.TopStart)
                    .background(Color.Black.copy(alpha = 0.4f), shape = androidx.compose.foundation.shape.CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }

            // 5. Sticky Bottom Button
            Button(
                onClick = onAddToCart,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Add to Cart - $${product.price}", fontSize = 18.sp)
            }
        }
    }
}
