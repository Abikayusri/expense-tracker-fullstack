package com.abika.expense.feature.category

import com.abika.expense.domain.category.CategoryType

data class CategoryFormState(
    val editingId: Long? = null,
    val name: String = "",
    val type: CategoryType = CategoryType.EXPENSE,
    val budgetText: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val fieldErrors: Map<String, String> = emptyMap(),
) {
    val isEditing: Boolean get() = editingId != null
}
