package com.shuham.urbaneats.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuham.urbaneats.domain.model.Product
import com.shuham.urbaneats.domain.usecase.cart.AddToCartUseCase
import com.shuham.urbaneats.domain.usecase.cart.CartSummary
import com.shuham.urbaneats.domain.usecase.cart.ClearCartUseCase
import com.shuham.urbaneats.domain.usecase.cart.GetCartUseCase
import com.shuham.urbaneats.domain.usecase.cart.RemoveFromCartUseCase
import com.shuham.urbaneats.domain.usecase.cart.UpdateCartQuantityUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class CartState(
    val summary: CartSummary = CartSummary(emptyList(), 0.0),
    val isLoading: Boolean = false
)

class CartViewModel(
    private val getCartUseCase: GetCartUseCase,
    private val updateCartQuantityUseCase: UpdateCartQuantityUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase,
    private val clearCartUseCase: ClearCartUseCase, // <--- NEW
    private val addToCartUseCase: AddToCartUseCase  // <--- NEW (For Upsells)
) : ViewModel() {

    private val _state = MutableStateFlow(CartState())
    val state = _state.asStateFlow()

    init {
        loadCart()
    }

    private fun loadCart() {
        viewModelScope.launch {
            getCartUseCase().collectLatest { summary ->
                _state.value = CartState(summary = summary)
            }
        }
    }

    fun incrementQuantity(productId: String, currentQuantity: Int) {
        viewModelScope.launch {
            updateCartQuantityUseCase(productId, currentQuantity + 1)
        }
    }

    fun decrementQuantity(productId: String, currentQuantity: Int) {
        viewModelScope.launch {
            if (currentQuantity > 1) {
                updateCartQuantityUseCase(productId, currentQuantity - 1)
            } else {
                // If quantity is 1, pressing minus should remove it (or ask confirmation)
                removeFromCartUseCase(productId)
            }
        }
    }


    // NEW: Clear All
    fun clearAllCart() {
        viewModelScope.launch {
            clearCartUseCase()
        }
    }

    // NEW: Add Upsell Item (Quick Add)
    fun addUpsellItem(product: Product) {
        viewModelScope.launch {
            // Add with default quantity 1 and no specific options
            addToCartUseCase(product, 1, "Standard", "")
        }
    }
}