package com.abika.expense.feature.category

import com.abika.expense.domain.category.Category

data class CategoriesState(
    val expenses: List<Category> = emptyList(),
    val incomes: List<Category> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val form: CategoryFormState? = null,
    val pendingDelete: Category? = null,
)
