package com.shuham.urbaneats.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                        color = MaterialTheme.colorScheme.onBackground // Theme Text
                    )
                    Text(
                        text = "Cancel",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary, // Theme Primary (Orange)
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onCancelClick() }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Search Field
                OutlinedTextField(
                    value = state.query,
                    onValueChange = onQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    placeholder = {
                        Text(
                            "Search food or restaurants",
                            color = MaterialTheme.colorScheme.onSurfaceVariant // Theme Secondary Text
                        )
                    },
                    leadingIcon = {
                        Icon(
                        Icons.Default.Search,
                        null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )  },
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        // Use Surface Variant (Light Gray in Light Mode, Darker Gray in Dark Mode)
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        disabledContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
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
                            color = MaterialTheme.colorScheme.onBackground,
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
                            color = MaterialTheme.colorScheme.onBackground,
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
                        item {
                            Text(
                                "Error: ${state.error}",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    } else if (state.results.isEmpty()) {
                        item {
                            Text(
                                "No results found",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        items(state.results) { product ->
                            FoodItemCard(
                                foodProduct = product,
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
            color = MaterialTheme.colorScheme.surfaceVariant, // Theme gray circle
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.History,
                contentDescription = null,
                modifier = Modifier.padding(8.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant // Theme icon color
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface // Theme text
        )
        Icon(
            Icons.Default.Close,
            null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
fun CuisineGrid() {
    val cuisines = listOf(
        "Italian" to "https://images.unsplash.com/photo-1595295333158-4742f28fbd85?w=500",
        "Chinese" to "https://images.unsplash.com/photo-1585032226651-759b368d7246?w=500",
        "Mexican" to "https://images.unsplash.com/photo-1626645738196-c2a7c87a8f58?w=500",
        "Indian" to "https://images.unsplash.com/photo-1710091691777-3115088962c4?q=80&w=687&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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