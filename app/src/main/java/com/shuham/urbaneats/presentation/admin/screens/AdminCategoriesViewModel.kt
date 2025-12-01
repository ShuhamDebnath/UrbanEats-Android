package com.shuham.urbaneats.presentation.admin.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shuham.urbaneats.domain.model.Category
import com.shuham.urbaneats.domain.repository.CategoryRepository
import com.shuham.urbaneats.util.NetworkResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminCategoryState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class AdminCategoriesViewModel(
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminCategoryState())
    val state = _state.asStateFlow()

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = categoryRepository.getCategories()
            when (result) {
                is NetworkResult.Success -> _state.update { it.copy(isLoading = false, categories = result.data ?: emptyList()) }
                is NetworkResult.Error -> _state.update { it.copy(isLoading = false, error = result.message) }
                else -> {}
            }
        }
    }

    fun saveCategory(id: String?, name: String, image: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = if (id == null) {
                categoryRepository.addCategory(name, image)
            } else {
                categoryRepository.updateCategory(id, name, image)
            }

            _state.update { it.copy(isLoading = false) }
            if (result is NetworkResult.Success) {
                loadCategories() // Refresh list
            } else {
                _state.update { it.copy(error = result.message) }
            }
        }
    }

    fun deleteCategory(id: String) {
        viewModelScope.launch {
            val result = categoryRepository.deleteCategory(id)
            if (result is NetworkResult.Success) loadCategories()
        }
    }
}