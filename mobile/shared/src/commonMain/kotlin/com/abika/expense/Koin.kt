package com.abika.expense

import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

/** Every feature's Koin module is listed here; the platforms only add their own extras. */
val appModules: List<Module> = emptyList()

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(appModules)
    }
}
