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
        onProfileClick = onProfileClick,
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
    searchQuery: String,
    searchResults: List<Product>,
    isSearching: Boolean,
    onSearchChange: (String) -> Unit,
    onProductClick: (Product) -> Unit, // This handles navigation
    onCartClick: () -> Unit,
    onProfileClick: () -> Unit,
    onFavoriteClick: (Product) -> Unit
) {
    // Dummy Categories for UI demo (Real app would fetch these)
    val categories = remember {
        listOf(
            Category("all", "All", "🍽️"),
            Category("burger", "Burgers", "🍔"),
            Category("pizza", "Pizza", "🍕"),
            Category("asian", "Asian", "🍣"),
            Category("dessert", "Dessert", "🍩")
        )
    }
    var selectedCategory by remember { mutableStateOf("all") }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background, // Use the Warm Cream
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
                // 1. Top Bar
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Deliver to", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.secondary)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Home, 123 Street", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            Icon(Icons.Default.ArrowDropDown, null, tint = MaterialTheme.colorScheme.primary)
                        }
                    }

                    // Profile Picture Placeholder
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.size(40.dp).clickable { onProfileClick() },
                        shadowElevation = 4.dp
                    ) {
                        Icon(Icons.Default.Person, null, modifier = Modifier.padding(8.dp), tint = MaterialTheme.colorScheme.onSurface)
                    }
                }

                // 2. Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 8.dp),
                    placeholder = { Text("Find your craving...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    trailingIcon = { Icon(Icons.Default.Tune, null) }, // Filter icon
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    )
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 16.dp,
                bottom = 100.dp // Space for BottomNav
            ),
            modifier = Modifier.fillMaxSize()
        ) {

            // 3. Categories (Only show if not searching)
            if (!isSearching) {
                item {
                    CategorySection(
                        categories = categories,
                        selectedCategory = selectedCategory,
                        onCategoryClick = { selectedCategory = it }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    Text(
                        text = "Popular Restaurants",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // 4. The Content List
            val displayList = if (isSearching) searchResults else state.products

//            if (state.isLoading && state.products.isEmpty()) {
//                // Show 3 shimmers instead of Spinner
//                Column {
//                    repeat(3) {
//                        FoodItemShimmer()
//                    }
//                }
//            }
            if (displayList.isEmpty() && !state.isLoading) {
                item {
                    Text("No items found", modifier = Modifier.fillMaxWidth().padding(32.dp), color = Color.Gray)
                }
            }
            else {
                items(displayList) { product ->
                    Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
                        FoodItemCard(
                            foodProduct = product,
                            onAddClick = { },
                            onItemClick = { onProductClick(product) },
                            onFavoriteClick = { onFavoriteClick(product) } // <--- TRIGGER EVENT
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
        "",
        emptyList(),
        false,
        {},
        {},
        {},
        {},
        {}
    )
}