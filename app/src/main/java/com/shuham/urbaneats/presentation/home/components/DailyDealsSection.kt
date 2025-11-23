package com.shuham.urbaneats.presentation.home.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

@Composable
fun DailyDealsSection() {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(3) { // Dummy items for now
            Column(modifier = Modifier.width(280.dp)) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.height(160.dp).fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    AsyncImage(
                        model = "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=500", // Burger Image
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Daily Deals", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Fresh deals from local favorites.", color = Color.Gray, fontSize = 12.sp)
            }
        }
    }
}