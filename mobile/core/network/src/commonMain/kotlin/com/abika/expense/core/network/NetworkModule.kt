package com.abika.expense.core.network

import io.ktor.client.engine.HttpClientEngine
import org.koin.core.module.Module
import org.koin.dsl.module

internal expect fun createPlatformEngine(): HttpClientEngine

/** Needs an [ApiConfig] from the app module. */
val networkModule: Module = module {
    single { SessionEvents() }
    single { createHttpClient(createPlatformEngine(), get(), get()) }
    single { ApiClient(get(), get(), get()) }
}
