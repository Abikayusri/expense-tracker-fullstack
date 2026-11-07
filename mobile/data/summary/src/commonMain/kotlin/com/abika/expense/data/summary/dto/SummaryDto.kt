package com.abika.expense.data.summary.dto

import com.abika.expense.domain.summary.Summary
import kotlinx.serialization.Serializable

@Serializable
internal data class SummaryDto(
    val totalIncome: Long,
    val totalExpense: Long,
    val balance: Long,
    val perCategory: List<CategorySummaryDto>,
) {
    fun toDomain() = Summary(totalIncome, totalExpense, balance, perCategory.map { it.toDomain() })
}
