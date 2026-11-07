package com.abika.expense.data.transaction.dto

import com.abika.expense.domain.transaction.Transaction
import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
internal data class TransactionDto(
    val id: Long,
    val categoryId: Long,
    val amount: Long,
    val date: String,
    val note: String? = null,
) {
    fun toDomain() = Transaction(id, categoryId, amount, LocalDate.parse(date), note)
}
