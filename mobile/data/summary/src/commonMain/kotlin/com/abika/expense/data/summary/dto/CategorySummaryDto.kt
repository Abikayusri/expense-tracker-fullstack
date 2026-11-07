package com.abika.expense.data.summary.dto

import com.abika.expense.domain.category.CategoryType
import com.abika.expense.domain.summary.CategorySummary
import kotlinx.serialization.Serializable

@Serializable
internal data class CategorySummaryDto(
    val categoryId: Long,
    val name: String,
    val type: String,
    val spent: Long,
    val budget: Long? = null,
    val overBudget: Boolean,
) {
    fun toDomain() = CategorySummary(categoryId, name, CategoryType.valueOf(type), spent, budget, overBudget)
}
