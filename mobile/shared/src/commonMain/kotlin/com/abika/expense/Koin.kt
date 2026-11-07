package com.abika.expense

import com.abika.expense.core.datastore.dataStoreModule
import com.abika.expense.core.network.ApiConfig
import com.abika.expense.core.network.networkModule
import com.abika.expense.data.auth.authDataModule
import com.abika.expense.feature.auth.di.authFeatureModule
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

/** Every feature's Koin module is listed here; the platforms only add their own extras. */
val appModules: List<Module> = listOf(
    dataStoreModule,
    networkModule,
    authDataModule,
    authFeatureModule,
)

fun initKoin(apiConfig: ApiConfig, config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(module { single { apiConfig } })
        modules(appModules)
    }
}
