package com.abika.expense.data.auth

import com.abika.expense.domain.auth.AuthRepository
import org.koin.core.module.Module
import org.koin.dsl.module

val authDataModule: Module = module {
    single<AuthRepository> { AuthRepositoryImpl(get(), get(), getAll()) }
}
