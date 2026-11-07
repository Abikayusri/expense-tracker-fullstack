package com.abika.expense.domain.summary

data class Summary(
    val totalIncome: Long,
    val totalExpense: Long,
    val balance: Long,
    val perCategory: List<CategorySummary>,
)
