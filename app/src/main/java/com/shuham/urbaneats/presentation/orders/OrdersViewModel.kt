package com.shuham.urbaneats.presentation.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuham.urbaneats.domain.model.Order
import com.shuham.urbaneats.domain.usecase.order.GetOrdersUseCase
import com.shuham.urbaneats.util.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OrdersState(
    val orders: List<Order> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class OrdersViewModel(
    private val getOrdersUseCase: GetOrdersUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(OrdersState())
    val state = _state.asStateFlow()

    init {
        fetchOrders()
    }

    fun fetchOrders() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            when (val result = getOrdersUseCase()) {
                is NetworkResult.Success -> {
                    // Sort by newest first (assuming the list isn't sorted by backend)
                    val sorted = result.data?.reversed() ?: emptyList()
                    _state.update { it.copy(isLoading = false, orders = sorted) }
                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
                else -> Unit
            }
        }
    }
}