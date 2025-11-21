package com.shuham.urbaneats.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shuham.urbaneats.data.local.UserSession
import org.koin.androidx.compose.koinViewModel


//  --- Route ---
@Composable
fun ProfileRoute(
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = koinViewModel()
) {
    val user by viewModel.user.collectAsStateWithLifecycle()

    // Watch for logout (if user becomes null/empty ID)
    LaunchedEffect(user) {
        if (user != null && user!!.id == null) {
            onLogout()
        }
    }

    ProfileScreen(
        user = user,
        onLogout = {
            viewModel.logout()
            onLogout() // Trigger navigation immediately
        }
    )
}

// --- SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    user: UserSession?,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("My Profile") }) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Avatar Placeholder
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(60.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // User Info
            Text(
                text = user?.name ?: "Guest User",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = user?.email ?: "No Email",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.weight(1f))

            // Logout Button
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Log Out")
            }
        }
    }
}