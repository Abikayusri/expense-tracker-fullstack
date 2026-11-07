package com.abika.expense.data.auth

import com.abika.expense.domain.auth.AuthRepository
import com.abika.expense.domain.auth.DeviceRegistry
import com.abika.expense.domain.auth.LoginHook
import com.abika.expense.domain.auth.LogoutHook
import org.koin.core.module.Module
import org.koin.dsl.binds
import org.koin.dsl.module

/** Needs a [com.abika.expense.domain.auth.PushTokenProvider] from the platform module. */
val authDataModule: Module = module {
    single<DeviceRegistry> { DeviceRegistryImpl(get()) }
    single { PushDeviceHooks(get(), get()) } binds arrayOf(LoginHook::class, LogoutHook::class)
    single<AuthRepository> { AuthRepositoryImpl(get(), get(), getAll(), getAll()) }
}
