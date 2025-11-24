package com.shuham.urbaneats.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.shuham.urbaneats.presentation.components.FoodItemShimmer
import com.shuham.urbaneats.presentation.home.components.CategorySection
import com.shuham.urbaneats.presentation.home.components.DailyDealsSection
import com.shuham.urbaneats.presentation.home.components.RestaurantCard
import org.koin.androidx.compose.koinViewModel

// ==========================================
// 1. THE CONTAINER (Route)
// ==========================================
@Composable
fun HomeRoute(
    viewModel: HomeViewModel = koinViewModel(),
    onFoodClick: (Product) -> Unit,
    onCartClick: () -> Unit,
    onSearchClick: () -> Unit,
    onAddressClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val currentAddress by viewModel.currentAddress.collectAsStateWithLifecycle()

    HomeScreen(
        state = state,
        onRefresh = viewModel::refreshData,
        onProductClick = onFoodClick,
        onCartClick = onCartClick,
        onSearchClick = onSearchClick,
        onFavoriteClick = viewModel::toggleFavorite,
        onCategorySelect = viewModel::selectCategory,
        currentAddress = currentAddress,
        onAddressClick = onAddressClick
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
    onSearchClick: () -> Unit,
    onFavoriteClick: (Product) -> Unit,
    onCategorySelect: (String) -> Unit,
    currentAddress: String,
    onAddressClick: () -> Unit
) {

    Scaffold(
        containerColor = Color.White,
        topBar = {
            // --- FIXED HEADER SECTION (Address + Categories) ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .statusBarsPadding() // Handles the status bar space
            ) {
                // 1. Address Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.clickable { onAddressClick() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color.Black
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = currentAddress,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Surface(
                        shape = CircleShape,
                        color = Color(0xFFF5F5F5),
                        modifier = Modifier
                            .size(40.dp)
                            .clickable { onCartClick() }
                    ) {
                        Icon(
                            Icons.Default.ShoppingCart,
                            null,
                            modifier = Modifier.padding(8.dp),
                            tint = Color.Black
                        )
                    }
                }

                // 2. Categories (Now Fixed at Top)


                CategorySection(
                    categories = state.categories, // Use Data from VM
                    selectedCategory = state.selectedCategoryId, // Use State from VM
                    onCategoryClick = { onCategorySelect(it) } // Pass event to VM
                )

                // Optional: Divider to separate fixed header from scrolling content
                HorizontalDivider(thickness = 1.dp, color = Color.LightGray.copy(alpha = 0.2f))
            }
        }
    ) { innerPadding ->
        // --- SCROLLABLE CONTENT SECTION ---
        LazyColumn(
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding(), // Starts below the Categories
                bottom = 100.dp
            ),
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. Daily Deals
            item {
                Spacer(modifier = Modifier.height(16.dp)) // Top spacing
                DailyDealsSection()
                Spacer(modifier = Modifier.height(24.dp))
            }

            // 2. Popular Section Title
            item {
                Text(
                    text = "Popular Near You",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // 3. Vertical Product List
            if (state.isLoading && state.products.isEmpty()) {
                items(3) { FoodItemShimmer() }
            } else {
                items(state.products) { product ->
                    RestaurantCard(
                        product = product,
                        onClick = { onProductClick(product) }
                    )
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
        "",
        {}

    )
}