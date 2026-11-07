package com.abika.expense.android

import android.app.Application
import com.abika.expense.initKoin
import org.koin.android.ext.koin.androidContext

class ExpenseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin { androidContext(this@ExpenseApplication) }
    }
}
