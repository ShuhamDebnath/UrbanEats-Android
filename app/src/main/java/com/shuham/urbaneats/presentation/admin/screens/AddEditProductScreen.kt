package com.shuham.urbaneats.presentation.admin.screens

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import org.koin.androidx.compose.koinViewModel
import java.io.ByteArrayOutputStream

@Composable
fun AddEditProductRoute(
    productId: String?,
    onBackClick: () -> Unit,
    viewModel: AddEditProductViewModel = koinViewModel()
) {
    LaunchedEffect(productId) {
        viewModel.init(productId)
    }
    val state by viewModel.state.collectAsState()

    if (state.isSuccess) {
        LaunchedEffect(Unit) { onBackClick() }
    }

    AddEditProductScreen(
        state = state,
        onBackClick = onBackClick,
        viewModel = viewModel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductScreen(
    state: AddEditProductState,
    onBackClick: () -> Unit,
    viewModel: AddEditProductViewModel
) {
    val context = LocalContext.current

    // Image Picker Logic (Reuse from Profile)
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Format Image String correctly for Backend
            val rawBase64 = uriToBase64(context, it)
            if (rawBase64 != null) {
                // Must prepend data URI scheme
                val formattedBase64 = "data:image/jpeg;base64,$rawBase64"
                viewModel.onImageSelected(it.toString(), formattedBase64)
            }
        }
    }

    // Dropdown State
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (state.productId == null) "Add Product" else "Edit Product") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Image Picker
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray)
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (state.imageUrl.isNotBlank()) {
                        AsyncImage(
                            model = state.imageUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Image, null, Modifier.size(48.dp), tint = Color.White)
                            Text("Tap to add image", color = Color.White)
                        }
                    }
                }
            }

            // 2. Basic Info
            item {
                OutlinedTextField(
                    value = state.name, onValueChange = viewModel::onNameChange,
                    label = { Text("Product Name") }, modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.description, onValueChange = viewModel::onDescChange,
                    label = { Text("Description") }, modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = state.price, onValueChange = viewModel::onPriceChange,
                    label = { Text("Price ($)") }, modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Spacer(modifier = Modifier.height(8.dp))

                // FIX 2: Category Dropdown
                // Logic: Find the name of the selected category ID to display
                val selectedCategoryName = state.availableCategories.find { it.id == state.selectedCategory }?.name ?: "Select Category"

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedCategoryName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        state.availableCategories.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    // Important: Send the ID, not the Name
                                    viewModel.onCategoryChange(category.id)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            // 3. Sizes
            item {
                Text("Sizes", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            itemsIndexed(state.sizes) { index, size ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${size.name} (+$${size.price})", modifier = Modifier.weight(1f))
                    IconButton(onClick = { viewModel.removeSize(index) }) {
                        Icon(Icons.Default.Delete, "Remove", tint = Color.Red)
                    }
                }
            }
            item {
                AddItemRow(label = "Add Size", onAdd = viewModel::addSize)
            }

            // 4. Add-ons
            item {
                Text("Add-ons", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            itemsIndexed(state.addons) { index, addon ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${addon.name} (+$${addon.price})", modifier = Modifier.weight(1f))
                    IconButton(onClick = { viewModel.removeAddon(index) }) {
                        Icon(Icons.Default.Delete, "Remove", tint = Color.Red)
                    }
                }
            }
            item {
                AddItemRow(label = "Add Extra", onAdd = viewModel::addAddon)
            }

            // 5. Save Button
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = viewModel::saveProduct,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) CircularProgressIndicator(color = Color.White)
                    else Text("Save Product")
                }
                if (state.error != null) {
                    Toast.makeText(LocalContext.current, state.error, Toast.LENGTH_SHORT).show()
                    //Text(state.error!!, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
fun AddItemRow(label: String, onAdd: (String, Double) -> Unit) {
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = name, onValueChange = { name = it },
            label = { Text(label) }, modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        OutlinedTextField(
            value = price, onValueChange = { price = it },
            label = { Text("Price") }, modifier = Modifier.width(100.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        IconButton(onClick = {
            if(name.isNotBlank() && price.isNotBlank()) {
                onAdd(name, price.toDoubleOrNull() ?: 0.0)
                name = ""
                price = ""
            }
        }) {
            Icon(Icons.Default.Add, "Add", tint = MaterialTheme.colorScheme.primary)
        }
    }
}

// Helper function to convert Uri to Base64
fun uriToBase64(context: android.content.Context, uri: Uri): String? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()
        inputStream?.close()
        if (bytes != null) Base64.encodeToString(bytes, Base64.DEFAULT) else null
    } catch (e: Exception) { null }
}