package com.shuham.urbaneats.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shuham.urbaneats.domain.model.Product
import com.shuham.urbaneats.presentation.components.FoodItemCard
import org.koin.androidx.compose.koinViewModel

// ==========================================
// 1. THE CONTAINER (Route)
// ==========================================
@Composable
fun HomeRoute(
    viewModel: HomeViewModel = koinViewModel(),
    onFoodClick: (Product) -> Unit,
    onCartClick: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    HomeScreen(
        state = state,
        onRefresh = viewModel::refreshData,
        onProductClick = onFoodClick,
        onCartClick = onCartClick
    )
}

// ==========================================
// 2. THE UI (Stateless Screen)
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeState,
    onRefresh: () -> Unit,
    onProductClick: (Product) -> Unit,
    onCartClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("UrbanEats Menu") },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                    // Cart Icon
                    IconButton(onClick = onCartClick) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {

            if (state.products.isEmpty() && state.isLoading) {
                // Initial Load
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                // The List
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.products) { product ->
                        FoodItemCard(
                            product = product,
                            onAddClick = { onProductClick(product) }
                        )
                    }
                }
            }

            // Error Message (Overlay)
            if (state.errorMessage != null && state.products.isEmpty()) {
                Text(
                    text = state.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            // Subtle Loading Indicator for Refreshing (when data already exists)
            if (state.isLoading && state.products.isNotEmpty()) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                )
            }
        }
    }
}


// ==========================================
// 3. THE PREVIEW
// ==========================================


@Preview
@Composable
private fun HomeScreenPrev() {
    HomeScreen(HomeState(), {}, {}, {})
}