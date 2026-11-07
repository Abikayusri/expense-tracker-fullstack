package com.abika.expense.core.datastore

import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual val dataStorePlatformModule: Module = module {
    single { createDataStore { androidContext().filesDir.resolve(DATA_STORE_FILE).absolutePath } }
}
