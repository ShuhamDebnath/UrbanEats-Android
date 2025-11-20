package com.shuham.urbaneats.presentation.details

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun DetailScreen(
    foodId: Int,
    foodName: String,
    onBackClick: () -> Boolean,
    modifier: Modifier = Modifier
) {
    Text("$foodName $foodId")

}
