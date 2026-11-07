package com.abika.expense.feature.summary.di

import com.abika.expense.feature.summary.DashboardViewModel
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import kotlin.time.Clock

val summaryFeatureModule: Module = module {
    viewModel { DashboardViewModel(get()) { Clock.System.todayIn(TimeZone.currentSystemDefault()) } }
}
