package com.abika.expense.feature.transaction.di

import com.abika.expense.feature.transaction.TransactionsViewModel
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import kotlin.time.Clock

val transactionFeatureModule: Module = module {
    viewModel { TransactionsViewModel(get(), get()) { Clock.System.todayIn(TimeZone.currentSystemDefault()) } }
}
