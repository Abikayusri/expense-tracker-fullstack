package com.abika.expense.android

import android.app.Application
import com.abika.expense.android.push.BudgetNotifications
import com.abika.expense.android.push.FirebasePushTokenProvider
import com.abika.expense.core.network.ApiConfig
import com.abika.expense.domain.auth.PushTokenProvider
import com.abika.expense.initKoin
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

class ExpenseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        BudgetNotifications.createChannel(this)
        initKoin(ApiConfig(baseUrl = BuildConfig.API_BASE_URL, enableNetworkInspector = BuildConfig.DEBUG)) {
            androidContext(this@ExpenseApplication)
            modules(module { single<PushTokenProvider> { FirebasePushTokenProvider(androidContext()) } })
        }
    }
}
