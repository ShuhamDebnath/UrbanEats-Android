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
import com.shuham.urbaneats.ui.theme.UrbanGold
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
        containerColor = MaterialTheme.colorScheme.background // Theme Background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // 1. HEADER (Avatar + Name)
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer), // Theme Container
                contentAlignment = Alignment.Center
            ) {
                if (!user?.profileImage.isNullOrBlank()) {
                    AsyncImage(
                        model = user!!.profileImage,
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer // Theme Content
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = user?.name ?: "Guest User",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground // Theme Text
            )

            if (!user?.email.isNullOrBlank()) {
                Text(
                    text = user!!.email!!,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant // Theme Secondary Text
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Gold Member Chip
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant, // Subtle background
                shape = RoundedCornerShape(50),
                modifier = Modifier.height(32.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    // Using explicit gold color for badge as it's semantic
                    Text("★ Gold Member", color = UrbanGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 2. MENU OPTIONS (Cards)
            // Used varied container colors for icons to keep visual interest, but mapped to theme logic where possible
            // Or standardizing them to primaryContainer for consistency in dark mode

            ProfileMenuCard(
                icon = Icons.AutoMirrored.Filled.ReceiptLong,
                iconBgColor = MaterialTheme.colorScheme.primaryContainer,
                iconTint = MaterialTheme.colorScheme.primary,
                title = "My Orders",
                onClick = { onOptionClick("orders") },
                trailingContent = {
                    Surface(
                        color = MaterialTheme.colorScheme.errorContainer, // Light Red/Orange feel
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            "Reorder",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileMenuCard(
                icon = Icons.Default.Favorite,
                iconBgColor = MaterialTheme.colorScheme.errorContainer,
                iconTint = MaterialTheme.colorScheme.error,
                title = "Favorites",
                onClick = { onOptionClick("favorites") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileMenuCard(
                icon = Icons.Default.CreditCard,
                iconBgColor = MaterialTheme.colorScheme.secondaryContainer,
                iconTint = MaterialTheme.colorScheme.secondary,
                title = "Payment Methods",
                onClick = { onOptionClick("payment") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileMenuCard(
                icon = Icons.Default.LocationOn,
                iconBgColor = MaterialTheme.colorScheme.tertiaryContainer, // Or reuse primary
                iconTint = MaterialTheme.colorScheme.tertiary,
                title = "Addresses",
                onClick = { onOptionClick("addresses") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileMenuCard(
                icon = Icons.Default.Help,
                iconBgColor = MaterialTheme.colorScheme.surfaceVariant,
                iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
                title = "Help & Support",
                onClick = { onOptionClick("help") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProfileMenuCard(
                icon = Icons.Default.Settings,
                iconBgColor = MaterialTheme.colorScheme.surfaceVariant,
                iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
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
                    containerColor = MaterialTheme.colorScheme.errorContainer, // Light Red in Light, Dark Red in Dark
                    contentColor = MaterialTheme.colorScheme.onErrorContainer // Red Text
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
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), // Theme Surface
        elevation = CardDefaults.cardElevation(0.dp), // Flat style
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
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
                color = MaterialTheme.colorScheme.onSurface, // Theme Text
                modifier = Modifier.weight(1f)
            )
            if (trailingContent != null) {
                trailingContent()
            } else {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f), // Theme Gray
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}