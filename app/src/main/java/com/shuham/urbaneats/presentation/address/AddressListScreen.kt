package com.shuham.urbaneats.presentation.address

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shuham.urbaneats.domain.model.Address
import org.koin.androidx.compose.koinViewModel

@Composable
fun AddressListRoute(
    onBackClick: () -> Unit,
    onAddressSelected: (Address) -> Unit, // Callback for Checkout
    viewModel: AddressViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AddressListScreen(
        state = state,
        onBackClick = onBackClick,
        onAddAddress = viewModel::addAddress,
        onSelectAddress = { address ->
            viewModel.selectAddress(address)
            onBackClick()
        },
        onDeleteAddress = { address ->
            viewModel.deleteAddress(address.id)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddressListScreen(
    state: AddressState,
    onBackClick: () -> Unit,
    onAddAddress: (String, String) -> Unit,
    onSelectAddress: (Address) -> Unit,
    onDeleteAddress: (Address) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Addresses", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = Color(0xFFE65100),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, "Add Address")
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.addresses.isEmpty()) {
                Text(
                    "No addresses found",
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.Gray
                )
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.addresses) { address ->
                        AddressCard(
                            address,
                            isSelected = address.id == state.selectedId,
                            onClick = { onSelectAddress(address) },
                            onDelete = { onDeleteAddress(address) })
                    }
                }
            }
        }
    }

    if (showDialog) {
        AddAddressDialog(
            onDismiss = { showDialog = false },
            onConfirm = { label, full ->
                onAddAddress(label, full)
                showDialog = false
            }
        )
    }
}

@Composable
fun AddressCard(
    address: Address,
    isSelected: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFFFF3E0) else Color.White // Highlight selected
        ),
        border = if (isSelected) BorderStroke(1.dp, Color(0xFFE65100)) else null,
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (address.label == "Home") Icons.Default.Home else Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFFE65100)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(address.label, fontWeight = FontWeight.Bold)
                    Text(address.fullAddress, color = Color.Gray, fontSize = 14.sp)
                }
            }

            // Delete Button
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, "Delete", tint = Color.Gray)
            }
        }
    }
}

@Composable
fun AddAddressDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var label by remember { mutableStateOf("Home") }
    var address by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Address") },
        text = {
            Column {
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Label (e.g., Home, Work)") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Full Address") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (address.isNotBlank()) onConfirm(label, address) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE65100))
            ) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}