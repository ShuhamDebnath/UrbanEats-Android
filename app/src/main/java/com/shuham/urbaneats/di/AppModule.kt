package com.shuham.urbaneats.di


import com.shuham.urbaneats.data.remote.KtorClient
import com.shuham.urbaneats.data.repositoryImpl.AuthRepositoryImpl
import com.shuham.urbaneats.domain.repository.AuthRepository
import com.shuham.urbaneats.domain.usecase.auth.LoginUseCase
import com.shuham.urbaneats.domain.usecase.validation.ValidateEmailUseCase
import com.shuham.urbaneats.presentation.login.LoginViewModel
import okhttp3.internal.platform.Platform.Companion.get
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module


val appModule = module {



    // 1. Ktor Client (Engine)
    single { KtorClient.httpClient }

    // 2. Repository (The Critical Fix)
    //single<AuthRepository> { AuthRepositoryImpl(get()) }
    singleOf(::AuthRepositoryImpl) bind AuthRepository::class


    // 3. UseCases
    factory { ValidateEmailUseCase() }
    factory { LoginUseCase(get()) } // Koin now finds AuthRepository above

    // 4. ViewModel
    viewModelOf(::LoginViewModel)

}

