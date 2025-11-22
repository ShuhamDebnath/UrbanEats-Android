package com.shuham.urbaneats.di


import androidx.room.Room
import com.shuham.urbaneats.data.local.TokenManager
import com.shuham.urbaneats.data.local.UrbanEatsDatabase
import com.shuham.urbaneats.data.remote.KtorClient
import com.shuham.urbaneats.data.repository.AuthRepositoryImpl
import com.shuham.urbaneats.data.repository.CartRepositoryImpl
import com.shuham.urbaneats.data.repository.OrderRepositoryImpl
import com.shuham.urbaneats.data.repository.ProductRepositoryImpl
import com.shuham.urbaneats.domain.repository.AuthRepository
import com.shuham.urbaneats.domain.repository.CartRepository
import com.shuham.urbaneats.domain.repository.OrderRepository
import com.shuham.urbaneats.domain.repository.ProductRepository
import com.shuham.urbaneats.domain.usecase.auth.LoginUseCase
import com.shuham.urbaneats.domain.usecase.cart.AddToCartUseCase
import com.shuham.urbaneats.domain.usecase.cart.GetCartUseCase
import com.shuham.urbaneats.domain.usecase.cart.PlaceOrderUseCase
import com.shuham.urbaneats.domain.usecase.cart.RemoveFromCartUseCase
import com.shuham.urbaneats.domain.usecase.cart.UpdateCartQuantityUseCase
import com.shuham.urbaneats.domain.usecase.order.GetOrdersUseCase
import com.shuham.urbaneats.domain.usecase.product.GetFavoritesUseCase
import com.shuham.urbaneats.domain.usecase.product.GetMenuUseCase
import com.shuham.urbaneats.domain.usecase.product.GetProductDetailsUseCase
import com.shuham.urbaneats.domain.usecase.product.RefreshMenuUseCase
import com.shuham.urbaneats.domain.usecase.product.SearchProductsUseCase
import com.shuham.urbaneats.domain.usecase.product.ToggleFavoriteUseCase
import com.shuham.urbaneats.domain.usecase.validation.ValidateEmailUseCase
import com.shuham.urbaneats.presentation.cart.CartViewModel
import com.shuham.urbaneats.presentation.checkout.CheckoutViewModel
import com.shuham.urbaneats.presentation.details.DetailViewModel
import com.shuham.urbaneats.presentation.favorites.FavoritesViewModel
import com.shuham.urbaneats.presentation.home.HomeViewModel
import com.shuham.urbaneats.presentation.login.LoginViewModel
import com.shuham.urbaneats.presentation.orders.OrdersViewModel
import com.shuham.urbaneats.presentation.profile.ProfileViewModel
import com.shuham.urbaneats.presentation.splash.SplashViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.jvm.java


val appModule = module {


    // 1. Ktor Client (Engine)
    single { KtorClient.httpClient }

    // 2. Repository (The Critical Fix)
    //single<AuthRepository> { AuthRepositoryImpl(get()) } // ==> old method
    singleOf(::AuthRepositoryImpl) bind AuthRepository::class
    singleOf(::ProductRepositoryImpl) bind ProductRepository::class
    singleOf(::CartRepositoryImpl) bind CartRepository::class
    singleOf(::OrderRepositoryImpl) bind OrderRepository::class


    // 3. UseCases
    factoryOf(::ValidateEmailUseCase)
    factoryOf(::LoginUseCase)
    factoryOf(::GetMenuUseCase)
    factoryOf(::RefreshMenuUseCase)
    factoryOf(::GetProductDetailsUseCase)
    factoryOf(::AddToCartUseCase)
    factoryOf(::GetCartUseCase)
    factoryOf(::UpdateCartQuantityUseCase)
    factoryOf(::RemoveFromCartUseCase)
    factoryOf(::PlaceOrderUseCase)
    factoryOf(::SearchProductsUseCase)
    factoryOf(::GetOrdersUseCase)
    factoryOf(::ToggleFavoriteUseCase)
    factoryOf(::GetFavoritesUseCase)


    // 4. ViewModel
    viewModelOf(::LoginViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::DetailViewModel)
    viewModelOf(::CartViewModel)
    viewModelOf(::CheckoutViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::SplashViewModel)
    viewModelOf(::OrdersViewModel)
    viewModelOf(::FavoritesViewModel)

    // 1. Provide Database Instance (Singleton)
    single {
        Room.databaseBuilder(
            androidContext(),
            UrbanEatsDatabase::class.java,
            "urbaneats_db"
        ).fallbackToDestructiveMigration(false) // Wipes DB if you change schema (Good for dev)
            .build()
    }

    // 2. Provide DAO (So Repositories can use it directly)
    single { get<UrbanEatsDatabase>().productDao() }
    single { get<UrbanEatsDatabase>().cartDao() }

    // Add to AppModule
    single { TokenManager(androidContext()) }

}

