package com.shuham.urbaneats.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shuham.urbaneats.data.local.UserSession
import org.koin.androidx.compose.koinViewModel


// --- ROUTE ---
@Composable
fun ProfileRoute(
    onLogoutSuccess: () -> Unit,
    onOrdersClick: () -> Unit,
    onFavoritesClick: () -> Unit,
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
        onOptionClick = {option ->
            if (option == "orders") onOrdersClick()
            if (option == "favorites") onFavoritesClick()

        }
    )
}

// --- SCREEN ---
@Composable
fun ProfileScreen(
    user: UserSession?,
    onLogout: () -> Unit,
    onOptionClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .background(MaterialTheme.colorScheme.background) // Cream Background
            .verticalScroll(rememberScrollState())
    ) {
        // 1. THE CURVED HEADER
        Box(modifier = Modifier.fillMaxWidth().height(240.dp)) {
            // Orange Background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
            ) {
                // Edit Button (Top Right)
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                ) {
                    Icon(Icons.Default.Edit, null, tint = Color.White)
                }
            }

            // Avatar & Name (Overlapping)
            Column(
                modifier = Modifier.align(Alignment.BottomCenter),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar
                Surface(
                    shape = CircleShape,
                    border = androidx.compose.foundation.BorderStroke(4.dp, MaterialTheme.colorScheme.background),
                    modifier = Modifier.size(100.dp),
                    color = Color.LightGray
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(60.dp),
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = user?.name ?: "Guest User",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = user?.email ?: "Sign in to see profile",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2. MY ACCOUNT SECTION
        ProfileSectionTitle("My Account")
        ProfileOptionItem(Icons.Default.ShoppingBag, "Orders", onClick = { onOptionClick("orders") })
        ProfileOptionItem(Icons.Default.FavoriteBorder, "Favorites", onClick = { onOptionClick("favorites") })
        ProfileOptionItem(Icons.Default.LocationOn, "Addresses", onClick = { onOptionClick("addresses") })
        ProfileOptionItem(Icons.Default.CreditCard, "Payment Methods", onClick = { onOptionClick("payment") })

        Spacer(modifier = Modifier.height(24.dp))

        // 3. SETTINGS SECTION
        ProfileSectionTitle("Settings")
        ProfileOptionItem(Icons.Default.NotificationsNone, "Notifications", onClick = { })
        ProfileOptionItem(Icons.Default.Language, "Language", onClick = { })
        ProfileOptionItem(Icons.AutoMirrored.Filled.HelpOutline, "Help & Support", onClick = { })

        Spacer(modifier = Modifier.height(32.dp))

        // 4. LOGOUT BUTTON
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary // Orange Text
            ),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(50)
        ) {
            Text("Log Out", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(40.dp)) // Bottom Padding
    }
}

// --- COMPONENTS ---

@Composable
fun ProfileSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
    )
}

@Composable
fun ProfileOptionItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(16.dp)
        )
    }
}