package com.shuham.urbaneats.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuham.urbaneats.domain.model.Category
import com.shuham.urbaneats.domain.model.Product
import com.shuham.urbaneats.domain.repository.UserRepository
import com.shuham.urbaneats.domain.usecase.product.GetCategoriesUseCase
import com.shuham.urbaneats.domain.usecase.product.GetMenuUseCase
import com.shuham.urbaneats.domain.usecase.product.RefreshMenuUseCase
import com.shuham.urbaneats.domain.usecase.product.ToggleFavoriteUseCase
import com.shuham.urbaneats.util.NetworkResult
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class HomeState(
    val products: List<Product> = emptyList(),
    val categories: List<Category> = emptyList(), // <--- NEW
    val selectedCategoryId: String = "all",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
)

class HomeViewModel(
    private val getMenuUseCase: GetMenuUseCase,
    private val refreshMenuUseCase: RefreshMenuUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    // Holds the formatted string to show in UI (e.g., "123 Main St...")
    private val _currentAddress = MutableStateFlow("Select Address")
    val currentAddress = _currentAddress.asStateFlow()


    private var allProducts: List<Product> = emptyList()

    init {
        observeMenu()
        refreshData()
        loadCategories()
        observeSelectedAddress()
    }

    private fun observeMenu() {
        viewModelScope.launch {
            getMenuUseCase().collect { dbProducts ->
                allProducts = dbProducts // Update master list
                // Re-apply current filter whenever DB updates
                applyFilter(_state.value.selectedCategoryId)
            }
        }
    }

    // NEW: Filter Logic
    fun selectCategory(categoryId: String) {
        applyFilter(categoryId)
    }

    private fun applyFilter(categoryId: String) {
        val filteredList = if (categoryId == "all") {
            allProducts
        } else {
            // Filter products where category ID matches
            // Note: This assumes product.category stores the Category ID
            allProducts.filter { it.category == categoryId }
        }

        _state.update {
            it.copy(
                selectedCategoryId = categoryId,
                products = filteredList
            )
        }
    }

    fun refreshData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            refreshMenuUseCase()
            _state.update { it.copy(isLoading = false) }
        }
    }

    fun toggleFavorite(product: Product) {
        viewModelScope.launch {
            toggleFavoriteUseCase(product.id, !product.isFavorite)
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val result = getCategoriesUseCase()
            if (result is NetworkResult.Success) {
                val allCat = Category("all", "All", "")
                val list = listOf(allCat) + (result.data ?: emptyList())
                _state.update { it.copy(categories = list) }
            }
        }
    }

    private fun observeSelectedAddress() {
        viewModelScope.launch {
            // Fetch latest addresses from server first
            val addressResult = userRepository.getAddresses()
            val allAddresses = if (addressResult is NetworkResult.Success) addressResult.data ?: emptyList() else emptyList()

            // Observe the ID preference
            userRepository.getSelectedAddressId().collect { selectedId ->
                val match = allAddresses.find { it.id == selectedId }
                // If match found, show it. If not, default to first one or "Select Address"
                _currentAddress.value = match?.fullAddress ?: allAddresses.firstOrNull()?.fullAddress ?: "Select Address"
            }
        }
    }

}