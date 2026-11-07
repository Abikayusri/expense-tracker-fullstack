package com.abika.expense.feature.category.di

import com.abika.expense.feature.category.CategoriesViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val categoryFeatureModule: Module = module {
    viewModelOf(::CategoriesViewModel)
}
