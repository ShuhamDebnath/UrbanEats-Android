package com.shuham.urbaneats.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuham.urbaneats.domain.model.Product
import com.shuham.urbaneats.domain.usecase.product.GetMenuUseCase
import com.shuham.urbaneats.domain.usecase.product.RefreshMenuUseCase
import com.shuham.urbaneats.domain.usecase.product.SearchProductsUseCase
import com.shuham.urbaneats.domain.usecase.product.ToggleFavoriteUseCase
import com.shuham.urbaneats.util.NetworkResult
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeState(
    val products: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@OptIn(FlowPreview::class)
class HomeViewModel(
    private val getMenuUseCase: GetMenuUseCase,
    private val refreshMenuUseCase: RefreshMenuUseCase,
    private val searchProductsUseCase: SearchProductsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase

) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    // 1. Search Query State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    // 2. Search Results State
    private val _searchResults = MutableStateFlow<List<Product>>(emptyList())
    val searchResults = _searchResults.asStateFlow()

    // 3. UI Mode (Are we searching or viewing the feed?)
    private val _isSearching = MutableStateFlow(false)
    val isSearching = _isSearching.asStateFlow()

    init {
        // 1. Start Observing Local DB immediately
        observeMenu()
        // 2. Trigger Network Refresh
        refreshData()
        observeSearch()
    }


    private fun observeSearch() {
        viewModelScope.launch {
            _searchQuery
                .debounce(500L) // WAIT 500ms after user stops typing
                .distinctUntilChanged() // Don't search if text hasn't changed
                .collectLatest { query ->
                    if (query.isBlank()) {
                        _isSearching.value = false
                        _searchResults.value = emptyList()
                    } else {
                        _isSearching.value = true
                        search(query)
                    }
                }
        }
    }

    private suspend fun search(query: String) {
        _state.update { it.copy(isLoading = true) } // Reuse main loading state
        val result = searchProductsUseCase(query)
        when (result) {
            is NetworkResult.Success -> {
                _searchResults.value = result.data ?: emptyList()
                _state.update { it.copy(isLoading = false) }
            }
            is NetworkResult.Error -> {
                // Handle error silently or show toast
                _state.update { it.copy(isLoading = false) }
            }
            else -> {}
        }
    }

    fun onSearchTextChange(text: String) {
        _searchQuery.value = text
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

    fun toggleFavorite(product: Product) {
        viewModelScope.launch {
            // Toggle logic: if currently true, make false
            toggleFavoriteUseCase(product.id, !product.isFavorite)
        }
    }
}