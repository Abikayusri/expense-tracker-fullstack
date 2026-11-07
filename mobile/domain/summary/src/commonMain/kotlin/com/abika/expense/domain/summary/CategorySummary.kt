package com.abika.expense.domain.summary

import com.abika.expense.domain.category.CategoryType

data class CategorySummary(
    val categoryId: Long,
    val name: String,
    val type: CategoryType,
    val spent: Long,
    val budget: Long?,
    val overBudget: Boolean,
)
