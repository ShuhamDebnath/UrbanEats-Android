package com.shuham.urbaneats.presentation.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.shuham.urbaneats.domain.model.Product
import com.shuham.urbaneats.ui.theme.UrbanGold

@Composable
fun RestaurantCard(
    product: Product,
    onClick: () -> Unit
) {
    // 1. The Container Card
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp), // Spacing between items
        shape = RoundedCornerShape(20.dp), // Smooth corners
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp), // "Little Elevated"
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column{
            // 2. Image (Rounded inside the card)
            AsyncImage(
                model = product.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp , topEnd = 16.dp)) // Matches the card's roundness style
            )
            Column(
                modifier = Modifier.padding(12.dp) // Inner spacing (Frame effect)
            ) {

                // 3. Title
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                // 4. Metadata Row (Rating • Time • Price)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Star Icon
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = UrbanGold, // Gold Star
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    // Rating
                    Text(
                        text = "${product.rating}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    // Separator
                    Text(
                        text = "  •  ",
                        color = Color.LightGray,
                        fontWeight = FontWeight.Bold
                    )

                    // Time (Mock data for now, or add to Product model)
                    Text(
                        text = "20-30 min",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )

                    Text(
                        text = "  •  ",
                        color = Color.LightGray,
                        fontWeight = FontWeight.Bold
                    )

                    // Price
                    Text(
                        text = "$${product.price}",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}