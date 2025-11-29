package com.shuham.urbaneats.presentation.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import org.koin.androidx.compose.koinViewModel

@Composable
fun FavoritesRoute(
    onBackClick: () -> Unit,
    onFoodClick: (Product) -> Unit,
    viewModel: FavoritesViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    FavoritesScreen(
        state = state,
        onBackClick = onBackClick,
        onFoodClick = onFoodClick,
        onRemoveFavorite = viewModel::removeFromFavorites
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    state: FavoritesState,
    onBackClick: () -> Unit,
    onFoodClick: (Product) -> Unit,
    onRemoveFavorite: (Product) -> Unit
) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background, // Theme Background
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "My Favorites",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground // Theme Text
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background // Theme Background
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground // Theme Icon
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        if (state.favorites.isEmpty()) {
            Box(
                modifier = Modifier.padding(innerPadding).fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No favorites yet ❤️",
                    color = MaterialTheme.colorScheme.onSurfaceVariant, // Theme Gray
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    top = innerPadding.calculateTopPadding() + 16.dp,
                    bottom = 16.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                items(state.favorites) { product ->
                    FoodItemCard(
                        foodProduct = product,
                        onItemClick = { onFoodClick(product) },
                        onFavoriteClick = { onRemoveFavorite(product) }
                    )
                }
            }
        }
    }
}