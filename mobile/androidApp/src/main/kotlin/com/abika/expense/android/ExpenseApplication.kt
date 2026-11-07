package com.abika.expense.android

import android.app.Application
import com.abika.expense.core.network.ApiConfig
import com.abika.expense.initKoin
import org.koin.android.ext.koin.androidContext

class ExpenseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(ApiConfig(baseUrl = BuildConfig.API_BASE_URL, enableNetworkInspector = BuildConfig.DEBUG)) {
            androidContext(this@ExpenseApplication)
        }
    }
}
