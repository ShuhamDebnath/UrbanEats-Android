package com.shuham.urbaneats.presentation.address

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuham.urbaneats.domain.model.Address
import com.shuham.urbaneats.domain.repository.UserRepository
import com.shuham.urbaneats.util.NetworkResult
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AddressState(
    val addresses: List<Address> = emptyList(),
    val selectedId: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class AddressViewModel(private val repository: UserRepository) : ViewModel() {

    private val _state = MutableStateFlow(AddressState())
    val state = _state.asStateFlow()

    init {
        loadAddresses()
        observeSelection()
    }

    private fun observeSelection() {
        viewModelScope.launch {
            repository.getSelectedAddressId().collect { id ->
                _state.update { it.copy(selectedId = id) }
            }
        }
    }

    fun loadAddresses() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = repository.getAddresses()) {
                is NetworkResult.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            addresses = result.data ?: emptyList()
                        )
                    }
                }

                is NetworkResult.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }

                else -> _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun addAddress(label: String, fullAddress: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            // API Call
            when (val result = repository.addAddress(label, fullAddress)) {
                is NetworkResult.Success -> {
                    val newList = result.data ?: emptyList()
                    _state.update { it.copy(isLoading = false, addresses = newList) }

                    // Auto-select the newly added address
                    if (newList.isNotEmpty()) {
                        selectAddress(newList.first())
                    }
                    _state.update { it.copy(isLoading = false) }
                }

                is NetworkResult.Error -> {

                    _state.update { it.copy(isLoading = false, error = result.message) }
                }

                else -> {

                    _state.update { it.copy(isLoading = false) }
                }
            }
        }
    }


    fun deleteAddress(addressId: String) {
        viewModelScope.launch {
            // Optimistic update or loading...
            repository.deleteAddress(addressId)
            loadAddresses() // Refresh list
        }
    }

    fun selectAddress(address: Address) {
        viewModelScope.launch {
            // 1. Save ID to Local Storage
            repository.selectAddress(address.id)

            // 2. Update UI selection state
            _state.update { it.copy(selectedId = address.id) }
        }
    }
}