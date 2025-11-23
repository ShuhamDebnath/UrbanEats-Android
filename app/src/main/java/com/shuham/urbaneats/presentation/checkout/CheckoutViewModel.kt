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
    // CHANGE: Now carries the ID
    data class NavigateToSuccess(val orderId: String) : CheckoutEffect
    data class NavigateToFailure(val reason: String) : CheckoutEffect
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

                    val orderId = result.data ?: "unknown"

                    // Fire Background Worker with REAL ID
                    val workRequest = OneTimeWorkRequestBuilder<OrderStatusWorker>()
                        .setInputData(workDataOf("order_id" to orderId))
                        .build()
                    workManager.enqueue(workRequest)

                    // Send ID to UI
                    _effect.send(CheckoutEffect.NavigateToSuccess(orderId))
                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(isLoading = false) }
                    _effect.send(CheckoutEffect.NavigateToFailure(result.message ?: "Unknown Error"))
                }
                is NetworkResult.Loading -> { }
            }
        }
    }
}