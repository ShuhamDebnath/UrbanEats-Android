package com.shuham.urbaneats.presentation.admin.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuham.urbaneats.domain.model.Product
import com.shuham.urbaneats.domain.usecase.product.DeleteProductUseCase
import com.shuham.urbaneats.domain.usecase.product.GetMenuUseCase
import com.shuham.urbaneats.domain.usecase.product.RefreshMenuUseCase
import com.shuham.urbaneats.util.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminMenuState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class AdminMenuViewModel(
    private val getMenuUseCase: GetMenuUseCase,
    private val refreshMenuUseCase: RefreshMenuUseCase,
    private val deleteProductUseCase: DeleteProductUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AdminMenuState())
    val state = _state.asStateFlow()

    init {
        loadMenu()
    }

    fun loadMenu() {
        viewModelScope.launch {
            // Observe Local DB
            getMenuUseCase().collect { products ->
                _state.update { it.copy(products = products) }
            }
        }
        // Trigger Refresh
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            refreshMenuUseCase()
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = deleteProductUseCase(productId)
            _state.update { it.copy(isLoading = false) }

            if (result is NetworkResult.Error) {
                _state.update { it.copy(error = result.message) }
            }
        }
    }
}