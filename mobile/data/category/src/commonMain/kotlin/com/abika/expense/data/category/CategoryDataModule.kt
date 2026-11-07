package com.abika.expense.data.category

import com.abika.expense.domain.category.CategoryRepository
import org.koin.core.module.Module
import org.koin.dsl.module

val categoryDataModule: Module = module {
    single<CategoryRepository> { CategoryRepositoryImpl(get()) }
}
