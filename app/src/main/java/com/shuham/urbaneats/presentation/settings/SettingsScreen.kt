package com.shuham.urbaneats.presentation.settings


import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.shuham.urbaneats.data.local.UserSession
import org.koin.androidx.compose.koinViewModel
import kotlin.math.log

@Composable
fun SettingsRoute(
    onBackClick: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val currentUser by viewModel.userSession.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Control Dialogs from Route level based on ViewModel Events
    var showProfileDialog by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }

    // Handle Toasts
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SettingsEffect.ShowToast -> {
                    Toast.makeText(context, effect.message, Toast.LENGTH_LONG).show()
                }
                is SettingsEffect.CloseProfileDialog -> {
                    showProfileDialog = false // <--- CLOSE IT AUTOMATICALLY
                }
                is SettingsEffect.ClosePasswordDialog -> {
                    showPasswordDialog = false
                }
            }
        }
    }


    SettingsScreen(
        state = state,
        currentUser = currentUser,
        showProfileDialog = showProfileDialog, // Pass state down
        showPasswordDialog = showPasswordDialog,
        onShowProfileDialog = { showProfileDialog = it }, // Pass setter
        onShowPasswordDialog = { showPasswordDialog = it },
        onBackClick = onBackClick,
        onThemeChange = viewModel::setTheme,
        onUpdateProfile = viewModel::updateProfile,
        onChangePassword = viewModel::changePassword,
        onTogglePush = viewModel::togglePush,
        onToggleEmail = viewModel::toggleEmail,
        onToggleTheme = viewModel::toggleTheme
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsState,
    currentUser: UserSession?,
    showProfileDialog: Boolean,
    showPasswordDialog: Boolean,
    onShowProfileDialog: (Boolean) -> Unit,
    onShowPasswordDialog: (Boolean) -> Unit,
    onBackClick: () -> Unit,
    onThemeChange: (String) -> Unit,
    onUpdateProfile: (String, String?) -> Unit,
    onChangePassword: (String, String) -> Unit,
    onTogglePush: (Boolean) -> Unit,
    onToggleEmail: (Boolean) -> Unit,
    onToggleTheme: (Boolean) -> Unit
) {

    var showThemeDialog by remember { mutableStateOf(false) }
    //var showProfileDialog by remember { mutableStateOf(false) }
    //var showPasswordDialog by remember { mutableStateOf(false) }


    Scaffold(
        containerColor = MaterialTheme.colorScheme.background, // Theme Background
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Settings",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground // Theme Text
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            "Back",
                            tint = MaterialTheme.colorScheme.onBackground // Theme Icon
                        )
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
                subtitle = currentUser?.name,
                onClick = { onShowProfileDialog(true) }
            )
            Spacer(modifier = Modifier.height(12.dp))
            SettingsActionCard(
                icon = Icons.Default.Lock,
                title = "Change Password",
                onClick = { onShowPasswordDialog(true) }
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
            SettingsActionCard(
                icon = Icons.Default.DarkMode,
                title = "App Theme",
                subtitle = state.selectedTheme.replaceFirstChar { it.uppercase() },
                onClick = { showThemeDialog = true }
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
                color = MaterialTheme.colorScheme.onSurfaceVariant, // Theme Gray
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(24.dp))
        }
    }

// --- DIALOGS ---

    if (showThemeDialog) {
        ThemeSelectionDialog(
            currentTheme = state.selectedTheme,
            onThemeSelected = {
                onThemeChange(it)
                showThemeDialog = false
            },
            onDismiss = { showThemeDialog = false }
        )
    }


    if (showProfileDialog) {
        EditProfileDialog(
            currentName = currentUser?.name ?: "",
            currentImageUrl = currentUser?.profileImage,
            isLoading = state.isUpdating,
            // Only allow manual dismiss if NOT loading
            onDismiss = { if (!state.isUpdating) onShowProfileDialog(false) },
            onSave = { name, base64Image ->
                onUpdateProfile(name, base64Image)
                // Do NOT close here. Wait for ViewModel event.
            }
        )
    }

    if (showPasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { onShowPasswordDialog(false) },
            onConfirm = { old, new ->
                onChangePassword(old, new)
                // Do NOT close here. Wait for ViewModel event.
            }
        )
    }
}

// --- COMPONENTS ---

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary, // Theme Primary
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), // Theme Surface
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary, // Theme Primary
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface // Theme Text
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant // Theme Secondary Text
                    )
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant, // Theme Gray
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), // Theme Surface
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
                tint = MaterialTheme.colorScheme.primary, // Theme Primary
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onSurface // Theme Text
            )

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary,
                    uncheckedThumbColor = MaterialTheme.colorScheme.outline,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}

@Composable
fun EditProfileDialog(
    currentName: String,
    currentImageUrl: String?,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String?) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? -> imageUri = uri }

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        containerColor = MaterialTheme.colorScheme.surface, // Theme Surface
        title = {
            Text(
                "Edit Profile",
                color = MaterialTheme.colorScheme.onSurface // Theme Text
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant) // Theme Variant
                        .clickable(enabled = !isLoading) { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        AsyncImage(
                            model = imageUri, contentDescription = null,
                            contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()
                        )
                    } else if (!currentImageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = currentImageUrl, contentDescription = null,
                            contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            Icons.Default.Edit,
                            null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant // Theme Icon
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    label = { Text("Full Name") },
                    singleLine = true,
                    enabled = !isLoading,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val base64Image = imageUri?.let { uri ->
                        try {
                            val inputStream = context.contentResolver.openInputStream(uri)
                            val bytes = inputStream?.readBytes()
                            inputStream?.close()
                            if (bytes != null) Base64.encodeToString(bytes, Base64.DEFAULT) else null
                        } catch (e: Exception) { null }
                    }
                    onSave(name, base64Image)
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                } else {
                    Text("Save")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
            ) { Text("Cancel") }
        }
    )
}

@Composable
fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var oldPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }

    var oldVisible by remember { mutableStateOf(false) }
    var newVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }

    var error by remember { mutableStateOf<String?>(null) }

    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        focusedLabelColor = MaterialTheme.colorScheme.primary,
        cursorColor = MaterialTheme.colorScheme.primary,
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text("Change Password", color = MaterialTheme.colorScheme.onSurface) },
        text = {
            Column {
                // Old Password
                OutlinedTextField(
                    value = oldPass, onValueChange = { oldPass = it },
                    label = { Text("Old Password") }, singleLine = true,
                    visualTransformation = if (oldVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    colors = textFieldColors,
                    trailingIcon = {
                        IconButton(onClick = { oldVisible = !oldVisible }) {
                            Icon(
                                if (oldVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                "Toggle",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))

                // New Password
                OutlinedTextField(
                    value = newPass, onValueChange = { newPass = it },
                    label = { Text("New Password") }, singleLine = true,
                    visualTransformation = if (newVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    colors = textFieldColors,
                    trailingIcon = {
                        IconButton(onClick = { newVisible = !newVisible }) {
                            Icon(
                                if (newVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                "Toggle",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Confirm Password
                OutlinedTextField(
                    value = confirmPass, onValueChange = { confirmPass = it },
                    label = { Text("Confirm New Password") }, singleLine = true,
                    visualTransformation = if (confirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    isError = error != null,
                    colors = textFieldColors,
                    trailingIcon = {
                        IconButton(onClick = { confirmVisible = !confirmVisible }) {
                            Icon(
                                if (confirmVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                "Toggle",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )

                if (error != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newPass != confirmPass) error = "Passwords do not match"
                    else if (newPass.length < 6) error = "Password must be at least 6 characters"
                    else onConfirm(oldPass, newPass)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) { Text("Update") }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
            ) { Text("Cancel") }
        }
    )
}

@Composable
fun ThemeSelectionDialog(currentTheme: String, onThemeSelected: (String) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text("Choose Theme", color = MaterialTheme.colorScheme.onSurface) },
        text = {
            Column {
                ThemeOption("System Default", "system", currentTheme, onThemeSelected)
                ThemeOption("Light", "light", currentTheme, onThemeSelected)
                ThemeOption("Dark", "dark", currentTheme, onThemeSelected)
            }
        },
        confirmButton = {
            TextButton(
                onClick = onDismiss,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
            ) { Text("Cancel") }
        }
    )
}

@Composable
fun ThemeOption(text: String, value: String, currentTheme: String, onSelect: (String) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(48.dp)
            .selectable(
                selected = (text == currentTheme),
                onClick = { onSelect(value) },
                role = Role.RadioButton
            )
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = (value == currentTheme),
            onClick = null,
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary,
                unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        Spacer(Modifier.width(16.dp))
        Text(text, color = MaterialTheme.colorScheme.onSurface)
    }
}