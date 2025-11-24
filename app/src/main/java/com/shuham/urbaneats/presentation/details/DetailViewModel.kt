package com.shuham.urbaneats.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuham.urbaneats.domain.model.AddonOption
import com.shuham.urbaneats.domain.model.Product
import com.shuham.urbaneats.domain.model.SizeOption
import com.shuham.urbaneats.domain.usecase.cart.AddToCartUseCase
import com.shuham.urbaneats.domain.usecase.product.GetProductDetailsUseCase
import com.shuham.urbaneats.domain.usecase.product.ToggleFavoriteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetailState(
    val product: Product? = null,
    val isLoading: Boolean = true
)

class DetailViewModel(
    private val getProductDetailsUseCase: GetProductDetailsUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase

) : ViewModel() {

    private val _state = MutableStateFlow(DetailState())
    val state = _state.asStateFlow()

    fun loadProduct(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val product = getProductDetailsUseCase(id)
            _state.update { it.copy(product = product, isLoading = false) }
        }
    }

    fun addToCart(
        size: SizeOption,
        addons: Set<AddonOption>,
        quantity: Int,
        instructions: String
    ) {
        viewModelScope.launch {
            val currentProduct = _state.value.product
            if (currentProduct != null) {
                // Create a nice string summary of options
                val addonString = addons.joinToString(", ") { it.name }
                val optionsSummary = if (addonString.isNotEmpty()) "${size.name}, $addonString" else size.name

                // Calculate adjusted price (Base + Size + Addons)
                val unitPrice = currentProduct.price + size.price + addons.sumOf { it.price }

                // Save to DB
                addToCartUseCase(
                    product = currentProduct.copy(price = unitPrice), // Store the adjusted price!
                    quantity = quantity,
                    options = optionsSummary,
                    instructions = instructions
                )
            }
        }
    }

    fun toggleFavorite(product: Product) {
        viewModelScope.launch {
            toggleFavoriteUseCase(product.id, !product.isFavorite)
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val currentProduct = _state.value.product
            if (currentProduct != null) {
                val newStatus = !currentProduct.isFavorite

                // 1. Optimistically update UI immediately
                _state.update {
                    it.copy(product = currentProduct.copy(isFavorite = newStatus))
                }

                // 2. Update Database
                toggleFavoriteUseCase(currentProduct.id, newStatus)
            }
        }
    }
}