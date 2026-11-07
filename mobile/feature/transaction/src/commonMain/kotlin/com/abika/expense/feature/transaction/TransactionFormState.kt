package com.abika.expense.feature.transaction

import kotlinx.datetime.LocalDate

data class TransactionFormState(
    val date: LocalDate,
    val editingId: Long? = null,
    val categoryId: Long? = null,
    val amountText: String = "",
    val note: String = "",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val fieldErrors: Map<String, String> = emptyMap(),
) {
    val isEditing: Boolean get() = editingId != null
}
