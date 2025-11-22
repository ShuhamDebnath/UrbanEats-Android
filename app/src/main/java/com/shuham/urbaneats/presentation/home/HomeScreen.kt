package com.shuham.urbaneats.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shuham.urbaneats.domain.model.Product
import com.shuham.urbaneats.presentation.components.FoodItemCard
import com.shuham.urbaneats.presentation.components.FoodItemShimmer
import com.shuham.urbaneats.presentation.home.components.Category
import com.shuham.urbaneats.presentation.home.components.CategorySection
import org.koin.androidx.compose.koinViewModel

// ==========================================
// 1. THE CONTAINER (Route)
// ==========================================
@Composable
fun HomeRoute(
    viewModel: HomeViewModel = koinViewModel(),
    onFoodClick: (Product) -> Unit,
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSearchClick: () -> Unit // <--- New Navigation Event
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    HomeScreen(
        state = state,
        onRefresh = viewModel::refreshData,
        onProductClick = onFoodClick,
        onCartClick = onCartClick,
        onProfileClick = onProfileClick,
        onSearchClick = onSearchClick, // <--- Pass it down
        onFavoriteClick = viewModel::toggleFavorite
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
    onProfileClick: () -> Unit,
    onSearchClick: () -> Unit,
    onFavoriteClick: (Product) -> Unit
) {
    val categories = remember {
        listOf(Category("all", "All", "🍽️"), Category("burger", "Burgers", "🍔"), Category("pizza", "Pizza", "🍕"), Category("asian", "Asian", "🍣"), Category("dessert", "Dessert", "🍩"))
    }
    var selectedCategory by remember { mutableStateOf("all") }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            // Clean Top Bar (No Search Box)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Location / Title Section
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Deliver to",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Home, 123 St",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(top = innerPadding.calculateTopPadding() + 16.dp, bottom = 100.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                CategorySection(categories, selectedCategory) { selectedCategory = it }
                Spacer(modifier = Modifier.height(24.dp))
            }
            item {
                Text("Popular Restaurants", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 24.dp))
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (state.isLoading && state.products.isEmpty()) {
                items(3) { FoodItemShimmer() }
            } else {
                items(state.products) { product ->
                    Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                        FoodItemCard(
                            foodProduct = product,
                            onAddClick = { },
                            onItemClick = { onProductClick(product) },
                            onFavoriteClick = { onFavoriteClick(product) }
                        )
                    }
                }
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
        {},
        {},
        {},
        {},
        {},
        {},
    )
}