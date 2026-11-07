package com.abika.expense.data.summary

import com.abika.expense.domain.summary.SummaryRepository
import org.koin.core.module.Module
import org.koin.dsl.module

val summaryDataModule: Module = module {
    single<SummaryRepository> { SummaryRepositoryImpl(get()) }
}
