package com.shuham.urbaneats.presentation.home

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shuham.urbaneats.presentation.components.FoodItemCard
import com.shuham.urbaneats.domain.model.FoodProduct

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onFoodClick: (FoodProduct) -> Unit
) {
    val context = LocalContext.current

    // FIX 1: Generate data ONCE inside the remember block.
    // We use a standard List because we aren't adding/removing items yet.
    val foodProducts = remember {
        List(10) { index ->
            FoodProduct(
                id = index,
                name = "Burger King Special ${index + 1}",
                description = "Flame-grilled beef patty topped with fresh tomatoes, cut lettuce, mayo, ketchup, pickles, and white onions on a soft sesame seed bun.",
                price = 10.0 * (index + 1),
                rating = (3..5).random().toDouble(), // Random realistic rating
                imageUrl = "https://picsum.photos/seed/${index}/300/200" // Seed ensures image stays same on scroll
            )
        }
    }

    // FIX 2: Use Scaffold (optional but good practice) or proper contentPadding
    Scaffold(
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        LazyColumn(
            // FIX 3: Apply padding to the content, not the container (avoids clipping shadows)
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 16.dp,
                bottom = innerPadding.calculateBottomPadding() + 16.dp,
                start = 16.dp,
                end = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(12.dp), // Space between cards
            modifier = Modifier.consumeWindowInsets(innerPadding)
        ) {
            items(foodProducts) { foodProduct ->
                FoodItemCard(
                    foodProduct = foodProduct,
                    onAddClick = {

                        onFoodClick(foodProduct)
                        Toast.makeText(context, "Added ${foodProduct.name}", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}


@Preview
@Composable
private fun HomeScreenPrev() {

    HomeScreen(
        onFoodClick = {}
    )

}