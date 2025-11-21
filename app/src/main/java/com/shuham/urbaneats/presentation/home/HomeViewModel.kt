package com.shuham.urbaneats.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuham.urbaneats.domain.model.Product
import com.shuham.urbaneats.domain.usecase.product.GetMenuUseCase
import com.shuham.urbaneats.domain.usecase.product.RefreshMenuUseCase
import com.shuham.urbaneats.util.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class HomeViewModel(
    private val getMenuUseCase: GetMenuUseCase,
    private val refreshMenuUseCase: RefreshMenuUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init {
        // 1. Start Observing Local DB immediately
        observeMenu()

        // 2. Trigger Network Refresh
        refreshData()
    }

    private fun observeMenu() {
        viewModelScope.launch {
            getMenuUseCase().collect { dbProducts ->
                _state.update { it.copy(products = dbProducts) }
            }
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }

            val result = refreshMenuUseCase()

            when (result) {
                is NetworkResult.Success -> {
                    // We don't update products here!
                    // The Room DB will update, triggering 'observeMenu' above automatically.
                    _state.update { it.copy(isLoading = false) }
                }
                is NetworkResult.Error -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = result.message ?: "Unknown Error"
                        )
                    }
                }
                else -> Unit
            }
        }
    }
}