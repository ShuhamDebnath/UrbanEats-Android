package com.shuham.urbaneats.presentation.admin.screens

import android.net.Uri
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuham.urbaneats.domain.model.AddonOption
import com.shuham.urbaneats.domain.model.Category
import com.shuham.urbaneats.domain.model.Product
import com.shuham.urbaneats.domain.model.SizeOption
import com.shuham.urbaneats.domain.usecase.product.AddUpdateProductUseCase
import com.shuham.urbaneats.domain.usecase.product.GetCategoriesUseCase
import com.shuham.urbaneats.domain.usecase.product.GetProductDetailsUseCase
import com.shuham.urbaneats.util.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddEditProductState(
    val productId: String? = null,
    val name: String = "",
    val description: String = "",
    val price: String = "",
    val imageUrl: String = "", // Can be URL or local URI string
    val selectedCategory: String = "",
    val sizes: List<SizeOption> = emptyList(),
    val addons: List<AddonOption> = emptyList(),
    val availableCategories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)

class AddEditProductViewModel(
    savedStateHandle: SavedStateHandle,
    private val addUpdateProductUseCase: AddUpdateProductUseCase,
    private val getProductDetailsUseCase: GetProductDetailsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    // SafeArgs style navigation argument retrieval
    // Note: "productId" string "null" or real ID
    // If your nav logic passes null for add, adjust accordingly.
    // Usually safe args passes "null" string if not present or null object.
    // Here we assume we pass nullable ID via route or handle check.
    // For simplicity, let's assume we pass it via constructor or setter if koin allows,
    // but standard VM uses SavedStateHandle.
    // Let's assume we manually trigger load if ID exists.

    private val _state = MutableStateFlow(AddEditProductState())
    val state = _state.asStateFlow()

    // Temporary Base64 holder for new image
    private var newImageBase64: String? = null

    init {
        loadCategories()
    }

    fun init(productId: String?) {
        if (productId != null) {
            loadProduct(productId)
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            val result = getCategoriesUseCase()
            if (result is NetworkResult.Success) {
                _state.update { it.copy(availableCategories = result.data ?: emptyList()) }
            }
        }
    }

    private fun loadProduct(id: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, productId = id) }
            val product = getProductDetailsUseCase(id)
            if (product != null) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        name = product.name,
                        description = product.description,
                        price = product.price.toString(),
                        imageUrl = product.imageUrl,
                        selectedCategory = product.category,
                        sizes = product.sizes,
                        addons = product.addons
                    )
                }
            } else {
                _state.update { it.copy(isLoading = false, error = "Product not found") }
            }
        }
    }

    // Field Updates
    fun onNameChange(v: String) { _state.update { it.copy(name = v) } }
    fun onDescChange(v: String) { _state.update { it.copy(description = v) } }
    fun onPriceChange(v: String) { _state.update { it.copy(price = v) } }
    fun onCategoryChange(v: String) { _state.update { it.copy(selectedCategory = v) } }

    fun onImageSelected(uri: String, base64: String) {
        _state.update { it.copy(imageUrl = uri) } // Show local preview
        newImageBase64 = base64
    }

    // Dynamic List Management
    fun addSize(name: String, price: Double) {
        _state.update { it.copy(sizes = it.sizes + SizeOption(name, price)) }
    }
    fun removeSize(index: Int) {
        _state.update { it.copy(sizes = it.sizes.toMutableList().apply { removeAt(index) }) }
    }

    fun addAddon(name: String, price: Double) {
        _state.update { it.copy(addons = it.addons + AddonOption(name, price)) }
    }
    fun removeAddon(index: Int) {
        _state.update { it.copy(addons = it.addons.toMutableList().apply { removeAt(index) }) }
    }

    fun saveProduct() {
        Log.d("TAG", "saveProduct: pass1 ")
        val s = _state.value
        if (s.name.isBlank() || s.price.isBlank()) {
            _state.update { it.copy(error = "Name and Price are required") }
            return
        }
        Log.d("TAG", "saveProduct: pass2 ")

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            val product = Product(
                id = s.productId ?: "",
                name = s.name,
                description = s.description,
                price = s.price.toDoubleOrNull() ?: 0.0,
                imageUrl = newImageBase64 ?: s.imageUrl, // Send base64 if new, else old URL
                rating = 4.5,
                category = s.selectedCategory,
                sizes = s.sizes,
                addons = s.addons
            )
            Log.d("TAG", "saveProduct: pass3  product $product | isEdit = ${s.productId != null}")



            val result = addUpdateProductUseCase(product, isEdit = s.productId != null)

            when (result) {
                is NetworkResult.Success -> _state.update { it.copy(isLoading = false, isSuccess = true) }
                is NetworkResult.Error -> _state.update { it.copy(isLoading = false, error = result.message) }
                else -> {}
            }

            Log.d("TAG", "saveProduct: pass4 ")
        }
    }
}