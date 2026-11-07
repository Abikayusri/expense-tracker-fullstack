package com.abika.expense.feature.auth.di

import com.abika.expense.feature.auth.login.LoginViewModel
import com.abika.expense.feature.auth.register.RegisterViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val authFeatureModule: Module = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)
}
