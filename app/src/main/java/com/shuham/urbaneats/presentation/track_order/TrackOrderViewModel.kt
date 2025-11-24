package com.shuham.urbaneats.presentation.track_order

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuham.urbaneats.domain.model.Order
import com.shuham.urbaneats.domain.usecase.order.GetOrderDetailsUseCase
import com.shuham.urbaneats.util.NetworkResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TrackOrderState(
    val orderId: String = "",
    val currentStep: Int = 1,
    val estimatedTime: String = "Calculating...",
    val orderDetails: Order? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class TrackOrderViewModel(
    savedStateHandle: SavedStateHandle ,
    private val getOrderDetailsUseCase: GetOrderDetailsUseCase
) : ViewModel() {

    // Extract the ID from the Route argument (matches property name in TrackOrderRoute)
    private val orderId: String = checkNotNull(savedStateHandle["orderId"])

    private val _state = MutableStateFlow(TrackOrderState(orderId = orderId))
    val state = _state.asStateFlow()

    init {
        // In a real app, you would fetch the specific order status using this ID
        simulateTracking()
        loadOrderDetails()
    }

    private fun loadOrderDetails() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = getOrderDetailsUseCase(orderId)
            when (result) {
                is NetworkResult.Success -> {
                    _state.update { it.copy(isLoading = false, orderDetails = result.data) }
                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
                else -> {}
            }
        }
    }

    private fun simulateTracking() {
        viewModelScope.launch {
            _state.update { it.copy(currentStep = 1, estimatedTime = "25-35 min") }
            delay(5000)
            _state.update { it.copy(currentStep = 2, estimatedTime = "15-20 min") }
            delay(5000)
            _state.update { it.copy(currentStep = 3, estimatedTime = "5-10 min") }
            delay(5000)
            _state.update { it.copy(currentStep = 4, estimatedTime = "Arrived!") }
        }
    }
}