package com.shuham.urbaneats.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuham.urbaneats.domain.usecase.cart.CartSummary
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
    private val removeFromCartUseCase: RemoveFromCartUseCase
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
}