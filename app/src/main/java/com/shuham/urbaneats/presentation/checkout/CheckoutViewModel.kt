package com.shuham.urbaneats.presentation.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.shuham.urbaneats.data.worker.OrderStatusWorker
import com.shuham.urbaneats.domain.usecase.cart.CartSummary
import com.shuham.urbaneats.domain.usecase.cart.GetCartUseCase
import com.shuham.urbaneats.domain.usecase.cart.PlaceOrderUseCase
import com.shuham.urbaneats.util.NetworkResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CheckoutState(
    val summary: CartSummary = CartSummary(emptyList(), 0.0),
    val address: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed interface CheckoutEffect {
    data object NavigateToSuccess : CheckoutEffect
    data class ShowToast(val message: String) : CheckoutEffect
}

class CheckoutViewModel(
    private val getCartUseCase: GetCartUseCase,
    private val placeOrderUseCase: PlaceOrderUseCase,
    private val workManager: WorkManager
) : ViewModel() {

    private val _state = MutableStateFlow(CheckoutState())
    val state = _state.asStateFlow()

    private val _effect = Channel<CheckoutEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        // Load cart data to display total amount
        viewModelScope.launch {
            getCartUseCase().collectLatest { summary ->
                _state.update { it.copy(summary = summary) }
            }
        }
    }

    fun onAddressChange(newAddress: String) {
        _state.update { it.copy(address = newAddress, error = null) }
    }

    fun placeOrder() {
        val currentState = _state.value

        if (currentState.address.isBlank()) {
            _state.update { it.copy(error = "Please enter a delivery address") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val result = placeOrderUseCase(
                address = currentState.address,
                total = currentState.summary.totalPrice,
                items = currentState.summary.items
            )

            when (result) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(isLoading = false) }
                    // FIRE BACKGROUND WORKER
                    val workRequest = OneTimeWorkRequestBuilder<OrderStatusWorker>()
                        .setInputData(workDataOf("order_id" to "12345")) // In real app, use result.data.id
                        .build()

                    workManager.enqueue(workRequest)

                    _effect.send(CheckoutEffect.NavigateToSuccess)

                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(CheckoutEffect.ShowToast(result.message ?: "Order Failed"))
                }
                is NetworkResult.Loading -> { /* No-op */ }
            }
        }
    }
}