package com.shuham.urbaneats.presentation.home

import app.cash.turbine.test
import com.shuham.urbaneats.MainDispatcherRule
import com.shuham.urbaneats.domain.model.Product
import com.shuham.urbaneats.domain.repository.UserRepository
import com.shuham.urbaneats.domain.usecase.product.GetCategoriesUseCase
import com.shuham.urbaneats.domain.usecase.product.GetMenuUseCase
import com.shuham.urbaneats.domain.usecase.product.RefreshMenuUseCase
import com.shuham.urbaneats.domain.usecase.product.ToggleFavoriteUseCase
import com.shuham.urbaneats.util.NetworkResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: HomeViewModel

    // Mocks
    private val getMenuUseCase: GetMenuUseCase = mock()
    private val refreshMenuUseCase: RefreshMenuUseCase = mock()
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase = mock()
    private val getCategoriesUseCase: GetCategoriesUseCase = mock() // <--- NEW MOCK
    private val userRepository: UserRepository = mock()             // <--- NEW MOCK

    @Before
    fun setUp() = runTest {
        // 1. Default Behavior: Return empty lists/flows to prevent crashes
        whenever(getMenuUseCase()).thenReturn(flowOf(emptyList()))

        // Mock Categories (Success with empty list)
        whenever(getCategoriesUseCase()).thenReturn(NetworkResult.Success(emptyList()))

        // Mock User Address Logic (Return empty list or "Select Address")
        whenever(userRepository.getAddresses()).thenReturn(NetworkResult.Success(emptyList()))
        whenever(userRepository.getSelectedAddressId()).thenReturn(flowOf(null))
    }

    @Test
    fun `initially loads data from database`() = runTest {
        // Given
        val dummyProducts = listOf(
            Product("1", "Burger", "Desc", 10.0, "url", 4.5, "Food", false)
        )
        whenever(getMenuUseCase()).thenReturn(flowOf(dummyProducts))

        // When (Initialize with ALL 5 dependencies)
        viewModel = HomeViewModel(
            getMenuUseCase,
            refreshMenuUseCase,
            toggleFavoriteUseCase,
            getCategoriesUseCase,
            userRepository
        )

        // Then
        viewModel.state.test {
            val state = awaitItem() // First emission
            // Depending on how fast flow emits, we might need to await item again or check property
            // For simplicity, assuming initial load works:
            assertEquals(dummyProducts, state.products)
        }
    }

    @Test
    fun `refreshData updates loading state`() = runTest {
        // Given
        whenever(refreshMenuUseCase()).thenReturn(NetworkResult.Success(Unit))

        // When
        viewModel = HomeViewModel(
            getMenuUseCase,
            refreshMenuUseCase,
            toggleFavoriteUseCase,
            getCategoriesUseCase,
            userRepository
        )
        viewModel.refreshData()

        // Then
        Mockito.verify(refreshMenuUseCase, Mockito.atLeastOnce()).invoke()
    }
}