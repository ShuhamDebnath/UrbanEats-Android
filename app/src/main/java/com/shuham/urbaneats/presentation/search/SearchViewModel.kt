package com.shuham.urbaneats.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuham.urbaneats.domain.model.Product
import com.shuham.urbaneats.domain.usecase.product.SearchProductsUseCase
import com.shuham.urbaneats.util.NetworkResult
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SearchState(
    val query: String = "",
    val results: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val isSearching: Boolean = false, // True if user has typed something
    val error: String? = null
)

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val searchProductsUseCase: SearchProductsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state = _state.asStateFlow()

    init {
        observeSearchInput()
    }

    private fun observeSearchInput() {
        viewModelScope.launch {
            _state.map { it.query }
                .distinctUntilChanged()
                .debounce(500L) // Wait 500ms
                .collectLatest { query ->
                    if (query.isBlank()) {
                        _state.update { it.copy(isSearching = false, results = emptyList(), error = null) }
                    } else {
                        _state.update { it.copy(isSearching = true, error = null) }
                        performSearch(query)
                    }
                }
        }
    }

    private suspend fun performSearch(query: String) {
        _state.update { it.copy(isLoading = true) }
        val result = searchProductsUseCase(query)

        when (result) {
            is NetworkResult.Success -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        results = result.data ?: emptyList(),
                        error = null
                    )
                }
            }
            is NetworkResult.Error -> {
                // NOW WE SAVE THE ERROR
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = result.message ?: "Unknown Error"
                    )
                }
            }
            else -> _state.update { it.copy(isLoading = false) }
        }
    }

    fun onQueryChange(newQuery: String) {
        _state.update { it.copy(query = newQuery) }
    }
}