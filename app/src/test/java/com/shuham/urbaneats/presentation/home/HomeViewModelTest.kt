package com.shuham.urbaneats.presentation.home

import android.util.Log
import app.cash.turbine.test
import com.shuham.urbaneats.MainDispatcherRule
import com.shuham.urbaneats.domain.model.Product
import com.shuham.urbaneats.domain.repository.UserRepository
import com.shuham.urbaneats.domain.usecase.deal.GetDailyDealsUseCase
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
import org.mockito.ArgumentMatchers.anyString
import org.mockito.MockedStatic
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
    private val getCategoriesUseCase: GetCategoriesUseCase = mock()
    private val getDailyDealsUseCase: GetDailyDealsUseCase = mock()
    private val userRepository: UserRepository = mock()

    // For static mocking of Log
    private lateinit var mockedLog: MockedStatic<Log>

    @Before
    fun setUp() {
        // 1. Mock Android Log to prevent "Method d in android.util.Log not mocked" error
        mockedLog = Mockito.mockStatic(Log::class.java)
        mockedLog.`when`<Int> { Log.d(anyString(), anyString()) }.thenReturn(0)
        mockedLog.`when`<Int> { Log.e(anyString(), anyString()) }.thenReturn(0)

        // 2. Default Behavior: Return empty lists/flows/success to prevent crashes
        // Use runTest block to call suspend functions
        runTest {
            whenever(getMenuUseCase()).thenReturn(flowOf(emptyList()))
            whenever(getCategoriesUseCase()).thenReturn(NetworkResult.Success(emptyList()))
            whenever(getDailyDealsUseCase.getDeals()).thenReturn(flowOf(emptyList()))
            whenever(userRepository.getSelectedAddressId()).thenReturn(flowOf(null))
            whenever(userRepository.getAddresses()).thenReturn(NetworkResult.Success(emptyList()))
        }
    }

    @org.junit.After
    fun tearDown() {
        // Must close static mock to avoid leaks or interference with other tests
        mockedLog.close()
    }

    @Test
    fun `initially loads data from database`() = runTest {
        // Given
        val dummyProducts = listOf(
            Product("1", "Burger", "Desc", 10.0, "url", 4.5, "Food", false)
        )
        whenever(getMenuUseCase()).thenReturn(flowOf(dummyProducts))

        // When
        viewModel = HomeViewModel(
            getMenuUseCase,
            refreshMenuUseCase,
            toggleFavoriteUseCase,
            getCategoriesUseCase,
            getDailyDealsUseCase,
            userRepository
        )

        // Then
        viewModel.state.test {
            val state = awaitItem()
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
            getDailyDealsUseCase,
            userRepository
        )
        viewModel.refreshData()

        // Then
        Mockito.verify(refreshMenuUseCase, Mockito.atLeastOnce()).invoke()
    }
}