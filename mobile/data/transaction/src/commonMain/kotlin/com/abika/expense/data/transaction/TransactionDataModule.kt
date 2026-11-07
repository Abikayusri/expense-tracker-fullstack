package com.abika.expense.data.transaction

import com.abika.expense.domain.transaction.TransactionRepository
import org.koin.core.module.Module
import org.koin.dsl.module

val transactionDataModule: Module = module {
    single<TransactionRepository> { TransactionRepositoryImpl(get()) }
}
