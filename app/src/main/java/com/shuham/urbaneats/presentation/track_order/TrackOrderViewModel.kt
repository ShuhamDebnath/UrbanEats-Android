package com.shuham.urbaneats.presentation.track_order

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TrackOrderState(
    val orderId: String = "",
    val currentStep: Int = 1,
    val estimatedTime: String = "Calculating..."
)

class TrackOrderViewModel(
    savedStateHandle: SavedStateHandle // <--- Koin injects this automatically
) : ViewModel() {

    // Extract the ID from the Route argument (matches property name in TrackOrderRoute)
    private val orderId: String = checkNotNull(savedStateHandle["orderId"])

    private val _state = MutableStateFlow(TrackOrderState(orderId = orderId))
    val state = _state.asStateFlow()

    init {
        // In a real app, you would fetch the specific order status using this ID
        simulateTracking()
    }

    private fun simulateTracking() {
        viewModelScope.launch {
            _state.update { it.copy(currentStep = 1, estimatedTime = "25-35 min") }
            delay(3000)
            _state.update { it.copy(currentStep = 2, estimatedTime = "15-20 min") }
            delay(5000)
            _state.update { it.copy(currentStep = 3, estimatedTime = "5-10 min") }
            delay(5000)
            _state.update { it.copy(currentStep = 4, estimatedTime = "Arrived!") }
        }
    }
}