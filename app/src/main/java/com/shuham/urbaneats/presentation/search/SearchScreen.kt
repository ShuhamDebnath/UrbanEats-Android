package com.shuham.urbaneats.presentation.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.shuham.urbaneats.domain.model.Product
import com.shuham.urbaneats.presentation.components.FoodItemCard
import com.shuham.urbaneats.presentation.components.FoodItemShimmer
import org.koin.androidx.compose.koinViewModel

@Composable
fun SearchRoute(
    onProductClick: (Product) -> Unit,
    viewModel: SearchViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    SearchScreen(
        state = state,
        onQueryChange = viewModel::onQueryChange,
        onProductClick = onProductClick,
        onAddClick = { /* Quick Add */ },
        onFavoriteClick = { /* Handle Favorite in Search later */ }
    )
}

@Composable
fun SearchScreen(
    state: SearchState,
    onQueryChange: (String) -> Unit,
    onProductClick: (Product) -> Unit,
    onAddClick: (Product) -> Unit,
    onFavoriteClick: (Product) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            // Search Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Search",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = state.query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Burgers, Pizza, Sushi...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()) {

            if (!state.isSearching && state.query.isEmpty()) {
                // EMPTY STATE / RECENT SEARCHES
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Search,
                        null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Type to find food", color = Color.Gray)
                }
            } else {
                // RESULTS LIST
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // 2. LOADING STATE
                    if (state.isLoading) {
                        items(3) { FoodItemShimmer() }
                    }
                    // 3. ERROR STATE (New)
                    else if (state.error != null) {
                        item {
                            Text(
                                text = "Error: ${state.error}",
                                color = MaterialTheme.colorScheme.error,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                    // 4. NO RESULTS
                    else if (state.results.isEmpty()) {
                        item {
                            Text(
                                "No results found",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                color = Color.Gray
                            )
                        }
                    }
                    // 5. RESULTS LIST
                    else {
                        items(state.results) { product ->
                            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                                FoodItemCard(
                                    foodProduct = product,
                                    onAddClick = { onAddClick(product) },
                                    onItemClick = { onProductClick(product) },
                                    onFavoriteClick = { onFavoriteClick(product) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}