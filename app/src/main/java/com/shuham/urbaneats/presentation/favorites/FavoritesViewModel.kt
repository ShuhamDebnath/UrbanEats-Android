package com.shuham.urbaneats.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuham.urbaneats.domain.model.Product
import com.shuham.urbaneats.domain.usecase.product.GetFavoritesUseCase
import com.shuham.urbaneats.domain.usecase.product.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class FavoritesState(
    val favorites: List<Product> = emptyList()
)

class FavoritesViewModel(
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(FavoritesState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            getFavoritesUseCase().collect { favs ->
                _state.value = FavoritesState(favorites = favs)
            }
        }
    }

    fun removeFromFavorites(product: Product) {
        viewModelScope.launch {
            toggleFavoriteUseCase(product.id, false)
        }
    }
}