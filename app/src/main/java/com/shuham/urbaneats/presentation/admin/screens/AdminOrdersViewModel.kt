package com.shuham.urbaneats.presentation.admin.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuham.urbaneats.domain.model.Order
import com.shuham.urbaneats.domain.usecase.order.GetAllOrdersUseCase
import com.shuham.urbaneats.domain.usecase.order.UpdateOrderStatusUseCase
import com.shuham.urbaneats.util.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminOrdersState(
    val allOrders: List<Order> = emptyList(), // Master list
    val filteredOrders: List<Order> = emptyList(), // Shown list
    val selectedTab: String = "Pending", // Pending, Preparing, Delivery, Delivered
    val isLoading: Boolean = false,
    val error: String? = null
)

class AdminOrdersViewModel(
    private val getAllOrdersUseCase: GetAllOrdersUseCase,
    private val updateOrderStatusUseCase: UpdateOrderStatusUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AdminOrdersState())
    val state = _state.asStateFlow()

    init {
        loadOrders()
    }

    fun loadOrders() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = getAllOrdersUseCase()) {
                is NetworkResult.Success -> {
                    val orders = result.data ?: emptyList()
                    _state.update {
                        it.copy(
                            isLoading = false,
                            allOrders = orders
                        )
                    }
                    filterOrders(_state.value.selectedTab)
                }
                is NetworkResult.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
                else -> {}
            }
        }
    }

    fun onTabSelected(tab: String) {
        _state.update { it.copy(selectedTab = tab) }
        filterOrders(tab)
    }

    private fun filterOrders(status: String) {
        val all = _state.value.allOrders
        val filtered = if (status == "All") all else all.filter { it.status.equals(status, ignoreCase = true) }
        _state.update { it.copy(filteredOrders = filtered) }
    }

    fun updateStatus(orderId: String, newStatus: String) {
        viewModelScope.launch {
            // Optimistic update
            val result = updateOrderStatusUseCase(orderId, newStatus)
            if (result is NetworkResult.Success) {
                loadOrders() // Refresh list to ensure sync
            }
        }
    }
}