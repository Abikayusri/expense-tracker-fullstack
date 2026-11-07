package com.abika.expense.core.datastore

import org.koin.core.module.Module
import org.koin.dsl.bind
import org.koin.dsl.module

internal expect val dataStorePlatformModule: Module

val dataStoreModule: Module = module {
    includes(dataStorePlatformModule)
    single { DataStoreTokenStore(get()) } bind TokenStore::class
}
