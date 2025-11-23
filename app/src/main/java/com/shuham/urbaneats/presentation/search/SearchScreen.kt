package com.shuham.urbaneats.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
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
        onFavoriteClick = { /* Handle Favorite */ },
        onCancelClick = { viewModel.onQueryChange("") } // Clear search
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    state: SearchState,
    onQueryChange: (String) -> Unit,
    onProductClick: (Product) -> Unit,
    onAddClick: (Product) -> Unit,
    onFavoriteClick: (Product) -> Unit,
    onCancelClick: () -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Search",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "Cancel",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFFE65100), // Orange
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onCancelClick() }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Search Field
                TextField(
                    value = state.query,
                    onValueChange = onQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    placeholder = { Text("Search food or restaurants", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF5F5F5),
                        unfocusedContainerColor = Color(0xFFF5F5F5),
                        disabledContainerColor = Color(0xFFF5F5F5),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true
                )
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {

            // SHOW EXPLORE CONTENT ONLY WHEN NOT SEARCHING
            if (!state.isSearching && state.query.isEmpty()) {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    // 1. Recent Searches
                    item {
                        Text(
                            text = "Recent Searches",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    items(listOf("Spicy Ramen", "McDonalds")) { recent ->
                        RecentSearchRow(text = recent)
                    }

                    item { Spacer(modifier = Modifier.height(24.dp)) }

                    // 2. Popular Cuisines Header
                    item {
                        Text(
                            text = "Popular Cuisines",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    // 3. Cuisine Grid (Implemented as FlowRow or manual Grid inside Column)
                    // Since LazyColumn can't hold LazyVerticalGrid directly without crashing,
                    // we build a fixed grid manually or use a single item with Column.
                    // Actually, better to swap the whole screen to LazyVerticalGrid?
                    // No, sticking to Column is safer for mixed content. We'll do rows of 2.

                    item {
                        CuisineGrid()
                    }
                }
            } else {
                // SEARCH RESULTS LIST (Existing logic)
                LazyColumn(
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.padding(horizontal = 16.dp).padding(top = 16.dp)
                ) {
                    if (state.isLoading) {
                        items(3) { FoodItemShimmer() }
                    } else if (state.error != null) {
                        item { Text("Error: ${state.error}", color = Color.Red) }
                    } else if (state.results.isEmpty()) {
                        item { Text("No results found", color = Color.Gray) }
                    } else {
                        items(state.results) { product ->
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

// --- COMPONENTS ---

@Composable
fun RecentSearchRow(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            color = Color(0xFFF0F0F0),
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = null,
                modifier = Modifier.padding(8.dp),
                tint = Color.Gray
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(text, modifier = Modifier.weight(1f), fontSize = 16.sp, color = Color.DarkGray)
        Icon(Icons.Default.Close, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
    }
}

@Composable
fun CuisineGrid() {
    val cuisines = listOf(
        "Italian" to "https://images.unsplash.com/photo-1595295333158-4742f28fbd85?w=500",
        "Chinese" to "https://images.unsplash.com/photo-1585032226651-759b368d7246?w=500",
        "Mexican" to "https://images.unsplash.com/photo-1626645738196-c2a7c87a8f58?w=500",
        "Indian" to "https://images.unsplash.com/photo-1585937421612-70a008356f36?w=500"
    )

    // Simple Grid implementation using Column + Rows
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Row 1
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CuisineCard(cuisines[0], Modifier.weight(1f))
            CuisineCard(cuisines[1], Modifier.weight(1f))
        }
        // Row 2
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CuisineCard(cuisines[2], Modifier.weight(1f))
            CuisineCard(cuisines[3], Modifier.weight(1f))
        }
    }
}

@Composable
fun CuisineCard(data: Pair<String, String>, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.height(160.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = data.second,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Gradient Overlay for text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                            startY = 100f
                        )
                    )
            )
            Text(
                text = data.first,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
            )
        }
    }
}