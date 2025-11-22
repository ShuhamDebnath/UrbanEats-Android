package com.shuham.urbaneats.presentation.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

data class Category(val id: String, val name: String, val icon: String = "🍔")

@Composable
fun CategorySection(
    categories: List<Category>,
    selectedCategory: String,
    onCategoryClick: (String) -> Unit
) {
    Column {
        Text(
            text = "Trending Cuisines",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(categories) { category ->
                CategoryChip(
                    category = category,
                    isSelected = category.id == selectedCategory,
                    onClick = { onCategoryClick(category.id) }
                )
            }
        }
    }
}

@Composable
fun CategoryChip(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
    val contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50)) // Pill shape
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = category.icon) // Emoji or Icon
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = category.name,
                color = contentColor,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}