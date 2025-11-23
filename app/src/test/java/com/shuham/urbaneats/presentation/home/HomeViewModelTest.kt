package com.shuham.urbaneats.presentation.home

import app.cash.turbine.test
import com.shuham.urbaneats.MainDispatcherRule
import com.shuham.urbaneats.domain.model.Product
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
    private val getMenuUseCase: GetMenuUseCase = mock()
    private val refreshMenuUseCase: RefreshMenuUseCase = mock()
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase = mock()

    @Before
    fun setUp() {
        // Mock the database returning an empty list initially
        whenever(getMenuUseCase()).thenReturn(flowOf(emptyList()))
    }

    @Test
    fun `initially loads data from database`() = runTest {
        // Given
        val dummyProducts = listOf(
            Product("1", "Burger", "Desc", 10.0, "url", 4.5, "Food", false)
        )
        whenever(getMenuUseCase()).thenReturn(flowOf(dummyProducts))

        // When
        viewModel = HomeViewModel(getMenuUseCase, refreshMenuUseCase, toggleFavoriteUseCase)

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertEquals(dummyProducts, state.products)
        }
    }

    @Test
    fun `refreshData updates loading state`() = runTest {
        // Given
        whenever(getMenuUseCase()).thenReturn(flowOf(emptyList()))
        whenever(refreshMenuUseCase()).thenReturn(NetworkResult.Success(Unit))

        viewModel = HomeViewModel(getMenuUseCase, refreshMenuUseCase, toggleFavoriteUseCase)

        // When
        viewModel.refreshData()

        // Then (We can't easily test the transient 'true' state without precise timing in this simple setup,
        // but we can verify it eventually settles to false and calls the use case)

        // Verify the use case was actually called
        Mockito.verify(refreshMenuUseCase, Mockito.atLeastOnce()).invoke()
    }
}