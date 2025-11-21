package com.shuham.urbaneats.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
    onCartClick: () -> Unit, // Add this from previous steps
    onProfileClick: () -> Unit // Add this
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val isSearching by viewModel.isSearching.collectAsStateWithLifecycle()

    HomeScreen(
        state = state,
        searchQuery = searchQuery,
        searchResults = searchResults,
        isSearching = isSearching,
        onSearchChange = viewModel::onSearchTextChange,
        onProductClick = onFoodClick,
        onCartClick = onCartClick,
        onProfileClick = onProfileClick
    )
}

// ==========================================
// 2. THE UI (Stateless Screen)
// ==========================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeState,
    searchQuery: String,
    searchResults: List<Product>,
    isSearching: Boolean,
    onSearchChange: (String) -> Unit,
    onProductClick: (Product) -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                // 1. Top Row (Title + Icons)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "UrbanEats",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row {
                        IconButton(onClick = onCartClick) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                        }
                        IconButton(onClick = onProfileClick) {
                            Icon(Icons.Default.Person, contentDescription = "Profile")
                        }
                    }
                }

                // 2. Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Search burgers, pizza...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.LightGray
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()) {

            // 3. Logic: Choose which list to show
            val displayList = if (isSearching) searchResults else state.products

            if (isSearching && displayList.isEmpty() && !state.isLoading) {
                // Empty State
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Search,
                        null,
                        tint = Color.Gray,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("No items found", color = Color.Gray)
                }
            } else {
                // Product List
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(displayList) { product ->
                        FoodItemCard(
                            product = product,
                            onProductClick = { onProductClick(product) },
                            onAddClick = {}

                        )
                    }
                }
            }

            // Loading Overlay
            if (state.isLoading) {
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
    HomeScreen(
        HomeState(),
        "",
        emptyList(),
        false,
        {},
        {},
        {},
        {},
    )
}