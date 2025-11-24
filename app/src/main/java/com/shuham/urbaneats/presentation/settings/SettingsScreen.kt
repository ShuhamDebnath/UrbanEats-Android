package com.shuham.urbaneats.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsRoute(
    onBackClick: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    SettingsScreen(
        state = state,
        onBackClick = onBackClick,
        onTogglePush = viewModel::togglePush,
        onToggleEmail = viewModel::toggleEmail,
        onToggleTheme = viewModel::toggleTheme
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsState,
    onBackClick: () -> Unit,
    onTogglePush: (Boolean) -> Unit,
    onToggleEmail: (Boolean) -> Unit,
    onToggleTheme: (Boolean) -> Unit
) {
    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Settings", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF5F5F5)
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // 1. ACCOUNT SETTINGS
            SettingsSectionTitle("Account")
            SettingsActionCard(
                icon = Icons.Default.Person,
                title = "Edit Profile",
                onClick = { /* Navigate to Edit Profile */ }
            )
            Spacer(modifier = Modifier.height(12.dp))
            SettingsActionCard(
                icon = Icons.Default.Lock,
                title = "Change Password",
                onClick = { /* Navigate to Change Password */ }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 2. NOTIFICATIONS
            SettingsSectionTitle("Notifications")
            SettingsSwitchCard(
                icon = Icons.Default.Notifications,
                title = "Push Notifications",
                checked = state.isPushEnabled,
                onCheckedChange = onTogglePush
            )
            Spacer(modifier = Modifier.height(12.dp))
            SettingsSwitchCard(
                icon = Icons.Default.Email,
                title = "Promotional Emails",
                checked = state.isEmailEnabled,
                onCheckedChange = onToggleEmail
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 3. PREFERENCES
            SettingsSectionTitle("Preferences")
            SettingsSwitchCard(
                icon = Icons.Default.DarkMode,
                title = "Dark Mode",
                checked = state.isDarkTheme,
                onCheckedChange = onToggleTheme
            )
            Spacer(modifier = Modifier.height(12.dp))
            SettingsActionCard(
                icon = Icons.Default.Language,
                title = "Language",
                subtitle = state.selectedLanguage,
                onClick = { /* Open Language Picker */ }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 4. LEGAL
            SettingsSectionTitle("Legal")
            SettingsActionCard(
                icon = Icons.Default.PrivacyTip,
                title = "Privacy Policy",
                onClick = { /* Open Webview */ }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "App Version 1.0.0",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// --- COMPONENTS ---

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = Color.Gray,
        modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
    )
}

@Composable
fun SettingsActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth().clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFFE65100), // Brand Orange
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, fontWeight = FontWeight.Medium)
                if (subtitle != null) {
                    Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = Color.LightGray,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun SettingsSwitchCard(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFFE65100),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = title, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFFE65100),
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color.LightGray.copy(alpha = 0.3f)
                )
            )
        }
    }
}