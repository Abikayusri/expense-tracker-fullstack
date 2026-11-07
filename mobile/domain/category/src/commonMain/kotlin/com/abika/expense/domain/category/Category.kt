package com.abika.expense.domain.category

data class Category(
    val id: Long,
    val name: String,
    val type: CategoryType,
    val monthlyBudget: Long?,
)
