package com.shuham.urbaneats.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuham.urbaneats.domain.model.Product
import com.shuham.urbaneats.domain.usecase.product.GetMenuUseCase
import com.shuham.urbaneats.domain.usecase.product.RefreshMenuUseCase
import com.shuham.urbaneats.domain.usecase.product.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class HomeViewModel(
    private val getMenuUseCase: GetMenuUseCase,
    private val refreshMenuUseCase: RefreshMenuUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    init {
        observeMenu()
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
            _state.update { it.copy(isLoading = true) }
            refreshMenuUseCase()
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun toggleFavorite(product: Product) {
        viewModelScope.launch {
            toggleFavoriteUseCase(product.id, !product.isFavorite)
        }
    }
}