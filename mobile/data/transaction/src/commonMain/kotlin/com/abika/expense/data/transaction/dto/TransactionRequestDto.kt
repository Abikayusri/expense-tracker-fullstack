package com.abika.expense.data.transaction.dto

import com.abika.expense.domain.transaction.TransactionInput
import kotlinx.serialization.Serializable

@Serializable
internal data class TransactionRequestDto(
    val categoryId: Long,
    val amount: Long,
    val date: String,
    val note: String?,
) {
    companion object {
        fun from(input: TransactionInput) =
            TransactionRequestDto(input.categoryId, input.amount, input.date.toString(), input.note)
    }
}
