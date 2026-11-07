package com.abika.expense.data.category.dto

import com.abika.expense.domain.category.Category
import com.abika.expense.domain.category.CategoryType
import kotlinx.serialization.Serializable

@Serializable
internal data class CategoryDto(val id: Long, val name: String, val type: String, val monthlyBudget: Long? = null) {
    fun toDomain() = Category(id, name, CategoryType.valueOf(type), monthlyBudget)
}
