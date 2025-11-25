package com.shuham.urbaneats.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.shuham.urbaneats.data.local.UserSession
import org.koin.androidx.compose.koinViewModel


// --- ROUTE ---
@Composable
fun ProfileRoute(
    onLogoutSuccess: () -> Unit,
    onOrdersClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onAddressClick: () -> Unit,
    onHelpClick: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val user by viewModel.userSession.collectAsStateWithLifecycle()

    LaunchedEffect(user) {
        if (user != null && user?.token == null) {
            onLogoutSuccess()
        }
    }

    ProfileScreen(
        user = user,
        onLogout = viewModel::logout,
        onOptionClick = { option ->
            if (option == "orders") onOrdersClick()
            if (option == "favorites") onFavoritesClick()
            if (option == "addresses") onAddressClick()
            if (option == "help") onHelpClick()
            if (option == "settings") onSettingsClick()
            // Add other navigation cases here
        }
    )
}

// --- SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    user: UserSession?,
    onLogout: () -> Unit,
    onOptionClick: (String) -> Unit
) {
    Scaffold(
        containerColor = Color(0xFFF5F5F5) // Light Gray Background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp), // Global horizontal padding
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // 1. HEADER (Avatar + Name)
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFA3C4BC)), // Muted Green fallback
                contentAlignment = Alignment.Center
            ) {
                if (!user?.profileImage.isNullOrBlank()) {
                    // Load Real Image from Cloudinary URL
                    AsyncImage(
                        model = user.profileImage,
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Show Default Icon if no image
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = user?.name ?: "Guest User",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            // Optional: Show Email for clarity
            if (!user?.email.isNullOrBlank()) {
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Gold Member Chip
            Surface(
                color = Color(0xFFFFF9C4), // Light Yellow
                shape = RoundedCornerShape(50),
                modifier = Modifier.height(32.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text("★ Gold Member", color = Color(0xFFFBC02D), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 2. MENU OPTIONS (Cards)

            // My Orders (With Reorder Chip)
            ProfileMenuCard(
                icon = Icons.AutoMirrored.Filled.ReceiptLong,
                iconBgColor = Color(0xFFFFE0B2), // Light Orange
                iconTint = Color(0xFFE65100),
                title = "My Orders",
                onClick = { onOptionClick("orders") },
                trailingContent = {
                    Surface(
                        color = Color(0xFFFFCCBC),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            "Reorder",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            color = Color(0xFFD84315),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileMenuCard(
                icon = Icons.Default.Favorite,
                iconBgColor = Color(0xFFFFCDD2), // Light Pink
                iconTint = Color(0xFFD32F2F),
                title = "Favorites",
                onClick = { onOptionClick("favorites") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileMenuCard(
                icon = Icons.Default.CreditCard,
                iconBgColor = Color(0xFFFFECB3), // Light Amber
                iconTint = Color(0xFFFF6F00),
                title = "Payment Methods",
                onClick = { onOptionClick("payment") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileMenuCard(
                icon = Icons.Default.LocationOn,
                iconBgColor = Color(0xFFFFE0B2),
                iconTint = Color(0xFFE65100),
                title = "Addresses",
                onClick = { onOptionClick("addresses") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileMenuCard(
                icon = Icons.AutoMirrored.Filled.Help,
                iconBgColor = Color(0xFFFFE0B2),
                iconTint = Color(0xFFE65100),
                title = "Help & Support",
                onClick = { onOptionClick("help") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileMenuCard(
                icon = Icons.Default.Settings,
                iconBgColor = Color(0xFFFFE0B2),
                iconTint = Color(0xFFE65100),
                title = "Settings",
                onClick = { onOptionClick("settings") }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 3. LOGOUT BUTTON
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFEBEE), // Very light pink
                    contentColor = Color(0xFFD32F2F) // Red text
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Text("Logout", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// --- COMPONENTS ---

@Composable
fun ProfileMenuCard(
    icon: ImageVector,
    iconBgColor: Color,
    iconTint: Color,
    title: String,
    onClick: () -> Unit,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Card(
        shape = RoundedCornerShape(24.dp), // Very round corners
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Circle
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(iconBgColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            if (trailingContent != null) {
                trailingContent()
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}